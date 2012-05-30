/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.wicket.util.crypt.Base64;

/**
 * Represents complete myExperiment file metadata (and data).
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "file")
public class File extends BaseResource {

    /** id. */
    private static final long serialVersionUID = 1547898914095065327L;

    /** Filename as provided by myExperiment. */
    private String filename;

    /** Content base64-encoded. */
    private String content;

    /** Content MIME type. */
    private String contentType;


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    /**
     * Return the content base64-decoded.
     * 
     * @return the raw content
     */
    public byte[] getContentDecoded() {
        return Base64.decodeBase64(content);
    }


    @XmlElement(name = "content-type")
    public String getContentType() {
        return contentType;
    }


    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    /**
     * Get file name.
     * 
     * @return a string that may not be a correct URI (may contain spaces etc.)
     */
    @XmlElement
    public String getFilename() {
        return filename.trim();
    }


    /**
     * Get the file name properly URI path encoded.
     * 
     * @return filename as an URI
     * @throws URISyntaxException
     *             if the URI could not be constructed
     */
    public URI getFilenameURI()
            throws URISyntaxException {
        return new URI(null, null, getFilename(), null, null);
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }


    @Override
    public String toString() {
        return String.format("file \"%s\"", filename);
    }

}
