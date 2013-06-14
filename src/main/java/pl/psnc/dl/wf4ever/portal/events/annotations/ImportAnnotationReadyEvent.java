package pl.psnc.dl.wf4ever.portal.events.annotations;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to import annotations in an RDF file.
 * 
 * @author piotrekhol
 * 
 */
public class ImportAnnotationReadyEvent extends AbstractAjaxEvent {

    /** The annotation body. */
    private final FileUpload uploadedFile;

    /** The resource that is annotated. */
    private final IModel<? extends Annotable> annotableModel;


    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param annotableModel
     *            the resource that is annotated
     * @param uploadedFile
     *            the annotation body
     */
    public ImportAnnotationReadyEvent(AjaxRequestTarget target, IModel<? extends Annotable> annotableModel,
            FileUpload uploadedFile) {
        super(target);
        this.annotableModel = annotableModel;
        this.uploadedFile = uploadedFile;
    }


    public IModel<? extends Annotable> getAnnotableModel() {
        return annotableModel;
    }


    public FileUpload getUploadedFile() {
        return uploadedFile;
    }
}
