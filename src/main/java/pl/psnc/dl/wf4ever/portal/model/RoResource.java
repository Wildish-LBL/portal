/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Calendar;

/**
 * @author piotrhol
 *
 */
public class RoResource
	implements AggregatedResource, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3584549388168924987L;

	private final URI uri;

	private final String name;


	/**
	 * @param uri
	 */
	public RoResource(URI uri, String name)
	{
		this.uri = uri;
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#getUri()
	 */
	@Override
	public URI getURI()
	{
		return uri;
	}


	/* (non-Javadoc)
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}


	@Override
	public String toString()
	{
		return getName();
	}


	/* (non-Javadoc)
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#isWorkflow()
	 */
	@Override
	public boolean isWorkflow()
	{
		//FIXME should rely on RO ontology
		return uri.getPath().endsWith(".t2flow");
	}


	@Override
	public Calendar getCreated()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getCreator()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
