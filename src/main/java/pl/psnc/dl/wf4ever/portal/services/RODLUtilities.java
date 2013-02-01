package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.purl.wf4ever.rosrs.client.Creator;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

/**
 * Various utility methods for RODL, not related directly to its REST APIs.
 * 
 * @author piotrekhol
 * 
 */
public final class RODLUtilities {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(RODLUtilities.class);


    /**
     * Private constructor.
     */
    private RODLUtilities() {
        //nope
    }


    /**
     * Get most recent Research Objects.
     * 
     * @param cnt
     *            number of ROs to get
     * @param sparqlEndpoint
     *            sparql endpoint URI
     * @param rodlURI
     *            RODL URI for resolving author names
     * @param usernames
     *            usernames cache
     * @return list of research objects
     * @throws IOException
     *             when cannot connect to SPARQL endpoint
     */
    public static List<ResearchObject> getMostRecentROs(URI sparqlEndpoint, URI rodlURI, Map<URI, Creator> usernames,
            int cnt)
            throws IOException {
        QueryExecution x = QueryExecutionFactory.sparqlService(sparqlEndpoint.toString(),
            MyQueryFactory.getxMostRecentROs(cnt));
        ResultSet results = x.execSelect();
        List<ResearchObject> roHeaders = new ArrayList<>();
        UserManagementService ums = MySession.get().getUms();
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            if (solution.getResource("ro") == null) {
                continue;
            }
            URI uri = URI.create(solution.getResource("ro").getURI());
            Literal creators = solution.getLiteral("creators");
            Set<Creator> authors = new HashSet<>();
            if (creators != null) {
                for (String creator : creators.getString().split(", ")) {
                    authors.add(Creator.get(ums, usernames, creator));
                }
            }
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
            ResearchObject ro = new ResearchObject(uri, null);
            ro.setCreated(created);
            ro.setCreators(authors);
            roHeaders.add(ro);
        }
        return roHeaders;
    }
}
