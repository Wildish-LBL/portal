package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;

/**
 * A utility class for loading SPARQL queries.
 * 
 * @author piotrek
 * 
 */
public final class MyQueryFactory {

    /** most recent ROs. */
    private static String xMostRecentROs = null;

    /** resources count. */
    private static String resourcesCount = null;

    /** get provenance traces. */
    private static String provenanceTraces;

    /** get all ROs. */
    private static String allROs;

    /** describe snapshot evolution. */
    private static String snapshotEvolution;

    /** describe live RO evolution. */
    private static String liveEvolution;

    /** describe archived RO evolution. */
    private static String archivedEvolution;

    /** check resource class. */
    private static String resourceClass;


    /**
     * Constructor.
     */
    private MyQueryFactory() {
        //nope
    }


    /**
     * Load the most recent ROs query.
     * 
     * @param limit
     *            how many ROs
     * @return SPARQL query
     * @throws IOException
     *             can't load the query file
     */
    public static String getxMostRecentROs(int limit)
            throws IOException {
        if (xMostRecentROs == null) {
            xMostRecentROs = loadQuery("xMostRecentROs.sparql");
        }
        return String.format(xMostRecentROs, limit);
    }


    /**
     * Load all ROs query.
     * 
     * @return SPARQL query
     * @throws IOException
     *             can't load the query file
     */
    public static String getAllROs()
            throws IOException {
        if (allROs == null) {
            allROs = loadQuery("allROs.sparql");
        }
        return allROs;
    }


    /**
     * Returns the query for the quantity of resources of a given class in the triplestore.
     * 
     * @param resourceClass
     *            where ro: is the Wf4Ever RO prefix
     * @return the query in the ARQ extension format (with "count")
     * @throws IOException
     *             can't load the query file
     */
    public static Query getResourcesCount(String resourceClass)
            throws IOException {
        if (resourcesCount == null) {
            resourcesCount = loadQuery("resourcesCount.sparql");
        }
        return QueryFactory.create(String.format(resourcesCount, resourceClass), Syntax.syntaxARQ);
    }


    /**
     * Get provenance traces.
     * 
     * @param researchObject
     *            the RO URI
     * @return SPARQL query
     * @throws IOException
     *             can't load the query file
     */
    public static Query getProvenanceTraces(String researchObject)
            throws IOException {
        if (provenanceTraces == null) {
            provenanceTraces = loadQuery("provenanceTraces.sparql");
        }
        return QueryFactory.create(String.format(provenanceTraces, researchObject), Syntax.syntaxARQ);
    }


    /**
     * Get the snapshot evolution.
     * 
     * @param researchObject
     *            the RO URI
     * @return SPARQL query
     * @throws IOException
     *             can't load the query file
     */
    public static Query getSnapshotEvolution(String researchObject)
            throws IOException {
        if (snapshotEvolution == null) {
            snapshotEvolution = loadQuery("snapshotEvolution.sparql");
        }
        return QueryFactory.create(String.format(snapshotEvolution, researchObject), Syntax.syntaxARQ);
    }


    /**
     * Get the live RO evolution.
     * 
     * @param researchObject
     *            the RO URI
     * @return SPARQL query
     * @throws IOException
     *             can't load the query file
     */
    public static Query getLiveEvolution(String researchObject)
            throws IOException {
        if (liveEvolution == null) {
            liveEvolution = loadQuery("liveEvolution.sparql");
        }
        return QueryFactory.create(String.format(liveEvolution, researchObject), Syntax.syntaxARQ);
    }


    /**
     * Get the archived RO evolution.
     * 
     * @param researchObject
     *            the RO URI
     * @return SPARQL query
     * @throws IOException
     *             can't load the query file
     */
    public static Query getArchivedEvolution(String researchObject)
            throws IOException {
        if (archivedEvolution == null) {
            archivedEvolution = loadQuery("archivedEvolution.sparql");
        }
        return QueryFactory.create(String.format(archivedEvolution, researchObject), Syntax.syntaxARQ);
    }


    /**
     * Get the resource class.
     * 
     * @param researchObject
     *            the RO URI
     * @return SPARQL query
     * @throws IOException
     *             can't load the query file
     */
    public static Query getResourceClass(String researchObject)
            throws IOException {
        if (resourceClass == null) {
            resourceClass = loadQuery("resourceClass.sparql");
        }
        return QueryFactory.create(String.format(resourceClass, researchObject), Syntax.syntaxARQ);
    }


    /**
     * Load a query from a file.
     * 
     * @param file
     *            filename
     * @return query as string
     * @throws IOException
     *             can't load the query file
     */
    private static String loadQuery(String file)
            throws IOException {
        InputStream is = MyQueryFactory.class.getClassLoader().getResourceAsStream("sparql/" + file);
        return IOUtils.toString(is, "UTF-8");
    }

}
