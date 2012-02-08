/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author piotrhol
 * 
 */
@XmlRootElement(name = "stability")
public class Stability
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1713651403977265592L;

	private double stabilityValue;


	/**
	 * @return the stabilityValue
	 */
	@XmlElement(name = "stabilityValue")
	public double getStabilityValue()
	{
		return stabilityValue;
	}


	/**
	 * @param stabilityValue
	 *            the stabilityValue to set
	 */
	public void setStabilityValue(double stabilityValue)
	{
		this.stabilityValue = stabilityValue;
	}

}
