package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Various utility methods for RODL, not related directly to its REST APIs.
 * 
 * @author piotrekhol
 * 
 */
public final class RODLUtilities {

    /** Logger. */
    @SuppressWarnings("unused")
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
     * @return list of research objects
     * @throws IOException
     *             when cannot connect to SPARQL endpoint
     */
    public static List<URI> getMostRecentROs(URI sparqlEndpoint, int cnt)
            throws IOException {
        QueryExecution x = QueryExecutionFactory.sparqlService(sparqlEndpoint.toString(),
            MyQueryFactory.getxMostRecentROs(cnt));
        ResultSet results = x.execSelect();
        List<URI> roHeaders = new ArrayList<>();
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            if (solution.getResource("ro") == null) {
                continue;
            }
            roHeaders.add(URI.create(solution.getResource("ro").getURI()));
        }
        return roHeaders;
    }
}
