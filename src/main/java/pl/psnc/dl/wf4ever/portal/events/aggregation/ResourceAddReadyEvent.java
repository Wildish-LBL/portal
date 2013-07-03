package pl.psnc.dl.wf4ever.portal.events.aggregation;

import java.net.URI;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;

/**
 * User wants to add a new resource.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceAddReadyEvent extends AbstractAjaxEvent {

    /** The file that should be uploaded. Null if the resource should only be referenced. */
    private final FileUpload uploadedFile;

    /** The RDF class of the resource. */
    private final ResourceType resourceClass;

    /** Resource URI, used only if the resources is added by reference. */
    private final URI resourceUri;


    /**
     * Constructor for a file uploaded with content.
     * 
     * @param target
     *            response target
     * @param uploadedFile
     *            the uploaded file
     * @param resourceClass
     *            resource class, if any
     */
    public ResourceAddReadyEvent(AjaxRequestTarget target, FileUpload uploadedFile, ResourceType resourceClass) {
        super(target);
        this.uploadedFile = uploadedFile;
        this.resourceClass = resourceClass;
        this.resourceUri = null;
    }


    /**
     * Constructor for a resource aggregated by reference only.
     * 
     * @param target
     *            response target
     * @param resourceURI
     *            resource URI
     * @param resourceClass
     *            resource class, if any
     */
    public ResourceAddReadyEvent(AjaxRequestTarget target, URI resourceURI, ResourceType resourceClass) {
        super(target);
        this.uploadedFile = null;
        this.resourceClass = resourceClass;
        this.resourceUri = resourceURI;
    }


    public FileUpload getUploadedFile() {
        return uploadedFile;
    }


    public ResourceType getResourceClass() {
        return resourceClass;
    }


    public URI getResourceUri() {
        return resourceUri;
    }

}
