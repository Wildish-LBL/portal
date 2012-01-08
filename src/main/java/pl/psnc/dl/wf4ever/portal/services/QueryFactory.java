/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author piotrek
 * 
 */
public class QueryFactory
{

	private static String xMostRecentROs = null;

	private static String resourcesCount = null;


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


	/**
	 * Returns the query for the quantity of resources of a given class in the triplestore
	 * 
	 * @param resourceClass
	 *            where ro: is the Wf4Ever RO prefix
	 * @return the query in the ARQ extension format (with "count")
	 * @throws IOException
	 */
	public static String getResourcesCount(String resourceClass)
		throws IOException
	{
		if (resourcesCount == null)
			resourcesCount = loadQuery("resourcesCount.sparql");
		return String.format(resourcesCount, resourceClass);
	}


	private static String loadQuery(String file)
		throws IOException
	{
		InputStream is = QueryFactory.class.getClassLoader().getResourceAsStream("sparql/" + file);
		return IOUtils.toString(is, "UTF-8");
	}

}
