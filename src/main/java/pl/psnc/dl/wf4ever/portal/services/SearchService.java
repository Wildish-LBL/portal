/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.purl.wf4ever.rosrs.client.Creator;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.SearchResult;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * This service fetches prepares a query using the OpenSearch API and sends it to dLibra, later it parses the responses.
 * 
 * @author piotrek
 * 
 */
public final class SearchService {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(SearchService.class);

    /** dLibra namespace. */
    private static final String DL_QUERY_NS = "http://dlibra.psnc.pl/opensearch/";

    /** date format for parsing the dates in search results. */
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

    private static final String SPARQL_REGEX = "REGEX(%s, \"%s\",\"i\") ";

    private static final String SPARQL_FILTER = "FILTER (%s).";

    private static final String SPARQL = "PREFIX ro: <http://purl.org/wf4ever/ro#>\n"
            + "PREFIX dcterms: <http://purl.org/dc/terms/>\n"
            + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
            + "PREFIX ore: <http://www.openarchives.org/ore/terms/>\n"
            + "\n"
            + "SELECT ?ro (sample(?creator) as ?thecreator) (min(?created) as ?mincreated) (count(distinct ?resource) as ?resCnt)\n"
            + "WHERE {\n" + "    ?ro a ro:ResearchObject ;\n" + "          dcterms:creator ?creator;\n"
            + "        dcterms:created ?created ;\n" + "        ore:aggregates ?resource ;\n"
            + "    OPTIONAL {?ro dcterms:title  ?title ;\n" + "                dcterms:description ?desc }\n"
            + "  %s \n" + "}\n" + "GROUP BY ?ro \n" + "ORDER BY DESC(?mincreated)\n" + "LIMIT 25";


    /**
     * Private constructor, this is a static service.
     */
    private SearchService() {
        //nope
    }


    /**
     * Performs a search in RODL.
     * 
     * @param rodlURI
     *            RODL URI
     * @param baseURI
     *            search module URI
     * @param keywords
     *            words to look for
     * @return list of search results
     * @throws IllegalArgumentException
     *             when the list of keywords is incorrect
     * @throws MalformedURLException
     *             the query string is incorrect
     * @throws FeedException
     *             the query string is incorrect
     * @throws IOException
     *             the query string is incorrect
     */
    @SuppressWarnings("unchecked")
    public static List<SearchResult> findByKeywords(URI rodlURI, URI baseURI, String keywords)
            throws IllegalArgumentException, MalformedURLException, FeedException, IOException {
        URI queryURI = new UriBuilderImpl().uri(baseURI).queryParam("searchTerms", keywords)
                .queryParam("aggregate", "false").queryParam("count", 50).build();

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(queryURI.toURL()));

        List<SyndEntry> entries = feed.getEntries();
        List<SearchResult> ros = new ArrayList<>();
        UserManagementService ums = MySession.get().getUms();
        Map<URI, Creator> usernames = MySession.get().getUsernames();
        for (SyndEntry entry : entries) {
            URI researchObjectURI = null;
            DateTime created = null;
            String title = null;
            Set<Creator> creators = new HashSet<>();
            double score = -1;
            List<Element> dlMarkup = (List<Element>) entry.getForeignMarkup();
            for (Element element : dlMarkup) {
                if (!DL_QUERY_NS.equals(element.getNamespaceURI())) {
                    continue;
                }
                switch (element.getName()) {
                    case "attribute":
                        switch (element.getAttributeValue("name")) {
                            case "Identifier":
                                researchObjectURI = URI.create(element.getValue());
                                break;
                            case "Creator":
                                creators.add(Creator.get(ums, usernames, element.getValue()));
                                break;
                            case "Created":
                                try {
                                    created = new DateTime(SDF.parse(element.getValue()).getTime());
                                } catch (ParseException e) {
                                    LOG.warn("Incorrect date", e);
                                    created = null;
                                }
                                break;
                            case "Title":
                                title = element.getValue();
                                break;
                            default:
                                break;
                        }
                        break;
                    case "score":
                        score = Double.parseDouble(element.getValue());
                        break;
                    default:
                        break;
                }
            }

            if (researchObjectURI != null && score != -1) {
                ResearchObject ro = new ResearchObject(researchObjectURI, null);
                ro.setCreated(created);
                ro.setCreators(creators);
                //                ro.setTitle(title);
                ros.add(new SearchResult(ro, score));
            }
        }

        return ros;
    }


    public static List<SearchResult> findUsingSparql(URI sparqlEndpointUri, String[] keywords) {
        List<SearchResult> searchResults = new ArrayList<>();
        StringBuilder filter = new StringBuilder();
        for (String keyword : keywords) {
            String[] regex = { String.format(SPARQL_REGEX, "?title", keyword),
                    String.format(SPARQL_REGEX, "?desc", keyword), String.format(SPARQL_REGEX, "str(?ro)", keyword) };
            filter.append(String.format(SPARQL_FILTER, StringUtils.join(regex, "||")));
        }
        String queryS = String.format(SPARQL, filter.toString());
        ResultSet results = QueryExecutionFactory.sparqlService(sparqlEndpointUri.toString(), queryS).execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            URI uri = URI.create(solution.get("ro").asResource().getURI());
            URI creator = URI.create(solution.get("thecreator").asResource().getURI());
            DateTime created = null;
            Object date = solution.getLiteral("created").getValue();
            if (date instanceof XSDDateTime) {
                created = new DateTime(((XSDDateTime) date).asCalendar().getTimeInMillis());
            } else {
                try {
                    created = new DateTime(ISODateTimeFormat.dateTime().parseDateTime(date.toString())
                            .toGregorianCalendar().getTimeInMillis());
                } catch (IllegalArgumentException e) {
                    LOG.warn("Don't know how to parse date: " + date);
                }
            }
            int resCnt = solution.getLiteral("resCnt").getInt();
            ResearchObject ro = new ResearchObject(uri, null);
            ro.setCreator(creator);
            ro.setCreated(created);
            SearchResult result = new SearchResult(ro, 1);
            result.setResourceCount(resCnt);
            searchResults.add(result);
        }
        return searchResults;
    }
}
