/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Piotr Hołubowicz
 *
 */
@XmlRootElement(name = "internal-pack-item")
public class InternalPackItem
	extends BaseResource

{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9038815722609845400L;

	private URI uri;

	private URI resource;

	private int id;

	private List<SimpleResourceHeader> items;


	public InternalPackItem()
	{

	}


	/**
	 * @return the uri
	 */
	@Override
	@XmlAttribute
	public URI getUri()
	{
		return uri;
	}


	/**
	 * @param uri the uri to set
	 */
	@Override
	public void setUri(URI uri)
	{
		this.uri = uri;
	}


	/**
	 * @return the resource
	 */
	@Override
	@XmlAttribute
	public URI getResource()
	{
		return resource;
	}


	/**
	 * @param resource the resource to set
	 */
	@Override
	public void setResource(URI resource)
	{
		this.resource = resource;
	}


	/**
	 * @return the title
	 */

	/**
	 * @return the id
	 */
	@Override
	@XmlElement
	public int getId()
	{
		return id;
	}


	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(int id)
	{
		this.id = id;
	}


	/**
	 * @return the item
	 */
	public SimpleResourceHeader getItem()
	{
		if (items == null || items.isEmpty())
			return null;
		else
			return items.get(0);
	}


	/**
	 * @return the items
	 */
	@XmlElementWrapper(name = "item")
	@XmlElements({ @XmlElement(name = "file", type = FileHeader.class),
			@XmlElement(name = "workflow", type = WorkflowHeader.class)})
	public List<SimpleResourceHeader> getItems()
	{
		return items;
	}


	/**
	 * @param items the items to set
	 */
	public void setItems(List<SimpleResourceHeader> items)
	{
		this.items = items;
	}
}
