/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author piotrhol
 * 
 */
@XmlRootElement(name = "recommendations")
public class Recommendations
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4635589213628595605L;

	private List<Recommendation> recommendations;


	/**
	 * @return the recommendations
	 */
	@XmlElement(name = "recommendation")
	public List<Recommendation> getRecommendations()
	{
		return recommendations;
	}


	/**
	 * @param recommendations
	 *            the recommendations to set
	 */
	public void setRecommendations(List<Recommendation> recommendations)
	{
		this.recommendations = recommendations;
	}
}
