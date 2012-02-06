/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.Calendar;

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
	 * @param creator
	 * @param created
	 * @param rSize
	 */
	public InternalResource(URI uri, Calendar created, String creator, String name, long size, Type type)
	{
		super(uri, created, creator, name, size, type);
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