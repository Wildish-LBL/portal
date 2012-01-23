/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author piotrhol
 * 
 */
public class Recommendation
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1713651403977265592L;

	private int itemID;

	private URI itemURI;

	private double strength;

	private URI userURI;


	/**
	 * @return the itemID
	 */
	@XmlElement
	public int getItemID()
	{
		return itemID;
	}


	/**
	 * @param itemID
	 *            the itemID to set
	 */
	public void setItemID(int itemID)
	{
		this.itemID = itemID;
	}


	/**
	 * @return the itemURI
	 */
	@XmlElement
	public URI getItemURI()
	{
		return itemURI;
	}


	/**
	 * @param itemURI
	 *            the itemURI to set
	 */
	public void setItemURI(URI itemURI)
	{
		this.itemURI = itemURI;
	}


	/**
	 * @return the strength
	 */
	@XmlElement
	public double getStrength()
	{
		return strength;
	}


	/**
	 * @param strength
	 *            the strength to set
	 */
	public void setStrength(double strength)
	{
		this.strength = strength;
	}


	/**
	 * @return the userURI
	 */
	@XmlElement
	public URI getUserURI()
	{
		return userURI;
	}


	/**
	 * @param userURI
	 *            the userURI to set
	 */
	public void setUserURI(URI userURI)
	{
		this.userURI = userURI;
	}

}
