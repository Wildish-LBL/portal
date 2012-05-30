/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents complete myExperiment file metadata. It could contain data but doesn't, since importing workflows is
 * handled by a dedicated Wf-RO transformation service.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "workflow")
public class Workflow extends BaseResource {

    /** id. */
    private static final long serialVersionUID = 3019438116219497825L;

    /** URI to workflow content. */
    private String contentUri;

    /** Content MIME type. */
    private String contentType;


    @XmlElement(name = "content-type")
    public String getContentType() {
        return contentType;
    }


    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    @XmlElement(name = "content-uri")
    public String getContentUri() {
        return contentUri;
    }


    public void setContentUri(String contentUri) {
        this.contentUri = contentUri;
    }


    @Override
    public String toString() {
        return String.format("workflow \"%s\"", getContentUri());
    }

}
