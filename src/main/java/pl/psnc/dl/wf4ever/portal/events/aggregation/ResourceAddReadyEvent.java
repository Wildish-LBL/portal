package pl.psnc.dl.wf4ever.portal.events.aggregation;

import java.net.URI;
import java.util.Collection;

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
    private final Collection<ResourceType> resourceTypes;

    /** Resource URI, used only if the resources is added by reference. */
    private final URI resourceUri;

    /** Custom MIME type. */
    private String mimeType;
    



    /**
     * Constructor for a file uploaded with content.
     * 
     * @param target
     *            response target
     * @param uploadedFile
     *            the uploaded file
     * @param resourceTypes
     *            resource class, if any
     * @param mimeType
     *            custom MIME type, if known
     */
    public ResourceAddReadyEvent(AjaxRequestTarget target, FileUpload uploadedFile,
            Collection<ResourceType> resourceTypes, String mimeType) {
        super(target);
        this.uploadedFile = uploadedFile;
        this.resourceTypes = resourceTypes;
        this.resourceUri = null;
        this.mimeType = mimeType;
    }


    /**
     * Constructor for a resource aggregated by reference only.
     * 
     * @param target
     *            response target
     * @param resourceURI
     *            resource URI
     * @param resourceTypes
     *            resource class, if any
     */
    public ResourceAddReadyEvent(AjaxRequestTarget target, URI resourceURI, Collection<ResourceType> resourceTypes) {
        super(target);
        this.uploadedFile = null;
        this.resourceTypes = resourceTypes;
        this.resourceUri = resourceURI;
    }


    public FileUpload getUploadedFile() {
        return uploadedFile;
    }


    public Collection<ResourceType> getResourceTypes() {
        return resourceTypes;
    }


    public URI getResourceUri() {
        return resourceUri;
    }


    public String getMimeType() {
        return mimeType;
    }


    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
