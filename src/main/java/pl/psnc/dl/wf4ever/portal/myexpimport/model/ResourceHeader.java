/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Piotr Hołubowicz
 *
 */
public abstract class ResourceHeader
	implements Serializable

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9038815722609845400L;

	private URI uri;

	private URI resource;

	private String title;


	public ResourceHeader()
	{

	}


	/**
	 * @return the uri
	 */
	@XmlAttribute
	public URI getUri()
	{
		return uri;
	}


	/**
	 * @param uri the uri to set
	 */
	public void setUri(URI uri)
	{
		this.uri = uri;
	}


	/**
	 * @return the resource
	 */
	@XmlAttribute
	public URI getResource()
	{
		return resource;
	}


	/**
	 * @param resource the resource to set
	 */
	public void setResource(URI resource)
	{
		this.resource = resource;
	}


	/**
	 * @return the title
	 */
	@XmlValue
	public String getTitle()
	{
		return title;
	}


	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}


	public abstract URI getResourceUrl();


	public abstract Class< ? extends BaseResource> getResourceClass();

}
