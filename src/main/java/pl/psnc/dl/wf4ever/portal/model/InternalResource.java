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
public class InternalResource
	extends AggregatedResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3584549388168924987L;


	/**
	 * @param uri
	 * @param name
	 * @param creators
	 * @param created
	 * @param rSize
	 */
	public InternalResource(URI uri, Calendar created, List<Creator> creators, String name, long size, Type type)
	{
		super(uri, created, creators, name, size, type);
	}


	public InternalResource()
	{
		super();
	}


	@Override
	public URI getDownloadURI()
	{
		return URI.create(getURI().toString() + "?content=true");
	}
}
