/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.net.URI;

/**
 * @author Piotr Hołubowicz
 *
 */
public class InternalPackItemHeader
	extends ResourceHeader
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1547898914095065327L;


	@Override
	public URI getResourceUrl()
	{
		return URI.create(getUri().toString() + "&elements=id,item");
	}


	@Override
	public Class<InternalPackItem> getResourceClass()
	{
		return InternalPackItem.class;
	}

}
