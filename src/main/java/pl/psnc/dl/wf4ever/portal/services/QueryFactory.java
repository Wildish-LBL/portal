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


	private static String loadQuery(String file)
		throws IOException
	{
		InputStream is = QueryFactory.class.getClassLoader().getResourceAsStream("sparql/" + file);
		return IOUtils.toString(is, "UTF-8");
	}

}
