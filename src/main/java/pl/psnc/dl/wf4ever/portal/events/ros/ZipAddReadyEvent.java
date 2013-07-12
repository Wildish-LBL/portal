package pl.psnc.dl.wf4ever.portal.events.ros;

import java.net.URI;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to create a new RO from a zip archive.
 * 
 * @author piotrekhol
 * 
 */
public class ZipAddReadyEvent extends AbstractAjaxEvent {

    /** The file that should be uploaded. Null if the resource should only be referenced. */
    private final FileUpload uploadedFile;

    /** Resource URI, used only if the resources is added by reference. */
    private final URI resourceUri;


    /**
     * Constructor for a file uploaded with content.
     * 
     * @param target
     *            response target
     * @param uploadedFile
     *            the uploaded file
     */
    public ZipAddReadyEvent(AjaxRequestTarget target, FileUpload uploadedFile) {
        super(target);
        this.uploadedFile = uploadedFile;
        this.resourceUri = null;
    }


    /**
     * Constructor for a resource aggregated by reference only.
     * 
     * @param target
     *            response target
     * @param resourceURI
     *            resource URI
     */
    public ZipAddReadyEvent(AjaxRequestTarget target, URI resourceURI) {
        super(target);
        this.uploadedFile = null;
        this.resourceUri = resourceURI;
    }


    public FileUpload getUploadedFile() {
        return uploadedFile;
    }


    public URI getResourceUri() {
        return resourceUri;
    }

}
