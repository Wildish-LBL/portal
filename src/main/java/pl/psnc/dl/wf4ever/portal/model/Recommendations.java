/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A list of recommendations from a recommendation service.
 * 
 * @author piotrhol
 * 
 */
@XmlRootElement(name = "recommendations")
public class Recommendations implements Serializable {

    /** id. */
    private static final long serialVersionUID = 4635589213628595605L;

    /** The list of recommendations. */
    private List<Recommendation> recommendations;


    @XmlElement(name = "recommendation")
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }


    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
