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
public class ResearchObject
	extends AggregatedResource
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6525552866849376681L;

	private final URI researchObjectURI;

	private final Calendar created;

	private final String creator;


	public ResearchObject(URI researchObjectURI, Calendar created, String creator)
	{
		this.researchObjectURI = researchObjectURI;
		this.created = created;
		this.creator = creator;
	}


	/**
	 * @return the researchObjectURI
	 */
	@Override
	public URI getURI()
	{
		return researchObjectURI;
	}


	/**
	 * @return the created
	 */
	@Override
	public Calendar getCreated()
	{
		return created;
	}


	@Override
	public String getName()
	{
		return "RO";
	}


	@Override
	public boolean isWorkflow()
	{
		return false;
	}


	@Override
	public String getCreator()
	{
		return creator;
	}


	@Override
	public long getSize()
	{
		return 0;
	}


	@Override
	public String getSizeFormatted()
	{
		return "--";
	}


	@Override
	public URI getDownloadURI()
	{
		return getURI();
	}
}
