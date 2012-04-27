/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;

/**
 * @author piotrek
 * 
 */
public class MyQueryFactory
{

	private static String xMostRecentROs = null;

	private static String resourcesCount = null;

	private static String workflowOutputs = null;

	private static String workflowInputs = null;

	private static String provenanceTraces;

	private static String allROs;


	/**
	 * @return the xMostRecentROs
	 * @throws IOException
	 */
	public static String getxMostRecentROs(int limit)
		throws IOException
	{
		if (xMostRecentROs == null)
			xMostRecentROs = loadQuery("xMostRecentROs.sparql");
		return String.format(xMostRecentROs, limit);
	}


	public static String getAllROs()
		throws IOException
	{
		if (allROs == null)
			allROs = loadQuery("allROs.sparql");
		return allROs;
	}


	/**
	 * Returns the query for the quantity of resources of a given class in the triplestore
	 * 
	 * @param resourceClass
	 *            where ro: is the Wf4Ever RO prefix
	 * @return the query in the ARQ extension format (with "count")
	 * @throws IOException
	 */
	public static Query getResourcesCount(String resourceClass)
		throws IOException
	{
		if (resourcesCount == null)
			resourcesCount = loadQuery("resourcesCount.sparql");
		return QueryFactory.create(String.format(resourcesCount, resourceClass), Syntax.syntaxARQ);
	}


	public static Query getWorkflowOutputs(String researchObject)
		throws IOException
	{
		if (workflowOutputs == null)
			workflowOutputs = loadQuery("workflowOutputs.sparql");
		return QueryFactory.create(String.format(workflowOutputs, researchObject), Syntax.syntaxARQ);
	}


	public static Query getWorkflowInputs(String researchObject)
		throws IOException
	{
		if (workflowInputs == null)
			workflowInputs = loadQuery("workflowInputs.sparql");
		return QueryFactory.create(String.format(workflowInputs, researchObject), Syntax.syntaxARQ);
	}


	public static Query getProvenanceTraces(String researchObject)
		throws IOException
	{
		if (provenanceTraces == null)
			provenanceTraces = loadQuery("provenanceTraces.sparql");
		return QueryFactory.create(String.format(provenanceTraces, researchObject), Syntax.syntaxARQ);
	}


	private static String loadQuery(String file)
		throws IOException
	{
		InputStream is = MyQueryFactory.class.getClassLoader().getResourceAsStream("sparql/" + file);
		return IOUtils.toString(is, "UTF-8");
	}

}
