package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to add a new resource.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceUpdateReadyEvent extends AbstractAjaxEvent {

    /** The file that should be uploaded. Null if the resource should only be referenced. */
    private final FileUpload uploadedFile;


    /**
     * Constructor for a file uploaded with content.
     * 
     * @param target
     *            response target
     * @param uploadedFile
     *            the uploaded file
     */
    public ResourceUpdateReadyEvent(AjaxRequestTarget target, FileUpload uploadedFile) {
        super(target);
        this.uploadedFile = uploadedFile;
    }


    public FileUpload getUploadedFile() {
        return uploadedFile;
    }

}
