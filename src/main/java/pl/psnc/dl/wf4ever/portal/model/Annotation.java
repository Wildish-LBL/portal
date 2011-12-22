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
public class Annotation
	extends AggregatedResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8821418401311175036L;

	private URI bodyURI;


	public Annotation(URI uri, Calendar created, String creator, String name, URI bodyURI)
	{
		super(uri, created, creator, name, 0);
		this.bodyURI = bodyURI;
	}


	public Annotation()
	{
		super();
	}


	/**
	 * @return the bodyURI
	 */
	public URI getBodyURI()
	{
		return bodyURI;
	}


	/* (non-Javadoc)
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#getDownloadURI()
	 */
	@Override
	public URI getDownloadURI()
	{
		return getURI();
	}


	/* (non-Javadoc)
	 * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#isWorkflow()
	 */
	@Override
	public boolean isWorkflow()
	{
		return false;
	}


	@Override
	public String getSizeFormatted()
	{
		return "--";
	}

}
