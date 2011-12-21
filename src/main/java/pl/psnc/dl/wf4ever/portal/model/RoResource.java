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

	/**
	 * @param uri
	 * @param name
	 * @param creator
	 * @param created
	 * @param rSize
	 */
	public RoResource(URI uri, Calendar created, String creator, String name, long size)
	{
		super(uri, created, creator, name, size);
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
	public URI getDownloadURI()
	{
		return URI.create(getURI().toString() + "?content=true");
	}
}
