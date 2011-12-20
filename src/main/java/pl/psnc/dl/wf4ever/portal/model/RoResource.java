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
	extends AggregatedResource
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3584549388168924987L;

	private final URI uri;

	private final String name;

	private final String creator;

	private final Calendar created;

	private final long size;


	/**
	 * @param uri
	 * @param name
	 * @param creator
	 * @param created
	 * @param rSize
	 */
	public RoResource(URI uri, String name, String creator, Calendar created, long rSize)
	{
		this.uri = uri;
		this.name = name;
		this.creator = creator;
		this.created = created;
		this.size = rSize;
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
		return created;
	}


	@Override
	public String getCreator()
	{
		return creator;
	}


	@Override
	public long getSize()
	{
		return size;
	}
}
