/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Represents short metadata of a myExperiment pack/file/workflow, as it appears in XML describing another resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public abstract class BaseResourceHeader implements Serializable {

    /** id. */
    private static final long serialVersionUID = -9038815722609845400L;

    /** XML with metadata URI. */
    private URI uri;

    /** Format-agnostic resource URI. */
    private URI resource;

    /** Resource title. */
    private String title;


    /**
     * Default constructor.
     */
    public BaseResourceHeader() {

    }


    @XmlAttribute
    public URI getUri() {
        return uri;
    }


    public void setUri(URI uri) {
        this.uri = uri;
    }


    @XmlAttribute
    public URI getResource() {
        return resource;
    }


    public void setResource(URI resource) {
        this.resource = resource;
    }


    @XmlValue
    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Return URI for a complete metadata resource.
     * 
     * @return URI
     */
    public abstract URI getResourceUrl();


    /**
     * Return a class of complete metadata resource, for deserialization.
     * 
     * @return Java class
     */
    public abstract Class<? extends BaseResource> getResourceClass();

}
