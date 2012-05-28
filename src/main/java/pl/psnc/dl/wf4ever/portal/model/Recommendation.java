/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a single recommendation coming out of a recommendation service.
 * 
 * @author piotrhol
 * 
 */
public class Recommendation implements Serializable {

    /** id. */
    private static final long serialVersionUID = 1713651403977265592L;

    /** Recommendation id. */
    private int itemID;

    /** Resource URI. */
    private URI resource;

    /** Recommendation strength. */
    private double strength;

    /** Resource creator URI. */
    private URI userURI;

    /** Resource title. */
    private String title;


    @XmlElement
    public int getItemID() {
        return itemID;
    }


    public void setItemID(int itemID) {
        this.itemID = itemID;
    }


    @XmlElement
    public URI getResource() {
        return resource;
    }


    public void setResource(URI itemURI) {
        this.resource = itemURI;
    }


    @XmlElement
    public double getStrength() {
        return strength;
    }


    public void setStrength(double strength) {
        this.strength = strength;
    }


    @XmlElement
    public URI getUserURI() {
        return userURI;
    }


    public void setUserURI(URI userURI) {
        this.userURI = userURI;
    }


    @XmlElement
    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

}
