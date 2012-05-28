package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.format.ISODateTimeFormat;

import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;

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
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            URI uri = URI.create(solution.getResource("ro").getURI());
            Literal creators = solution.getLiteral("creators");
            List<Creator> authors = new ArrayList<>();
            if (creators != null) {
                for (String creator : creators.getString().split(", ")) {
                    authors.add(RoFactory.getCreator(rodlURI, usernames, creator));
                }
            }
            Calendar created = null;
            Object date = solution.getLiteral("created").getValue();
            if (date instanceof XSDDateTime) {
                created = ((XSDDateTime) date).asCalendar();
            } else {
                try {
                    created = ISODateTimeFormat.dateTime().parseDateTime(date.toString()).toGregorianCalendar();
                } catch (IllegalArgumentException e) {
                    LOG.warn("Don't know how to parse date: " + date);
                }
            }
            roHeaders.add(new ResearchObject(uri, created, authors));
        }
        return roHeaders;
    }

}
