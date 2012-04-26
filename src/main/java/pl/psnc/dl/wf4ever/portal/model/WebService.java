/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

/**
 * @author piotrhol
 * 
 */
public class WebService
	extends AggregatedResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7357223157435615080L;


	public WebService(URI uri, Calendar created, List<Creator> creators, String name, long size)
	{
		super(uri, created, creators, name, size, Type.WEB_SERVICE);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#getDownloadURI()
	 */
	@Override
	public URI getDownloadURI()
	{
		return null;
	}


	@Override
	public String getSizeFormatted()
	{
		return null;
	}

}
