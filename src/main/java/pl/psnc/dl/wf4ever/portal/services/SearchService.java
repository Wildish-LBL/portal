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

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.Creator;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.SearchResult;

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
}
