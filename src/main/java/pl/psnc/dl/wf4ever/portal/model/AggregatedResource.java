/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;

/**
 * @author piotrhol
 *
 */
public class AggregatedResource
{

	private final URI uri;

	private final String name;


	/**
	 * @param uri
	 */
	public AggregatedResource(URI uri, String name)
	{
		this.uri = uri;
		this.name = name;
	}


	/**
	 * @return the uri
	 */
	public URI getUri()
	{
		return uri;
	}


	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}


	@Override
	public String toString()
	{
		return getName();
	}
}
