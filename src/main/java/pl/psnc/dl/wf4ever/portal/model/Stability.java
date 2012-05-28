/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a stability measure.
 * 
 * @author piotrhol
 * 
 */
@XmlRootElement(name = "stability")
public class Stability implements Serializable {

    /** id. */
    private static final long serialVersionUID = 1713651403977265592L;

    /** The stability measure. */
    private double stabilityValue;


    @XmlElement(name = "stabilityValue")
    public double getStabilityValue() {
        return stabilityValue;
    }


    public void setStabilityValue(double stabilityValue) {
        this.stabilityValue = stabilityValue;
    }

}
