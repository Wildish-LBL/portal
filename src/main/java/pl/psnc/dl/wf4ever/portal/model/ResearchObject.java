/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.request.UrlDecoder;

/**
 * @author piotrhol
 * 
 */
public class ResearchObject
	extends AggregatedResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6525552866849376681L;


	public ResearchObject(URI researchObjectURI, Calendar created, List<Creator> creators)
	{
		super(researchObjectURI, created, creators, UrlDecoder.PATH_INSTANCE.decode(researchObjectURI.resolve("..")
				.relativize(researchObjectURI).toString(), "UTF-8"));
	}


	public ResearchObject()
	{
		super();
	}


	@Override
	public URI getDownloadURI()
	{
		return getURI();
	}

}
