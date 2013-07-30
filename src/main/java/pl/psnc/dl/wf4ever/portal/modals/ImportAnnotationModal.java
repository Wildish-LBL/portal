package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationReadyEvent;

import com.google.common.eventbus.EventBus;

/**
 * Modal window for importing an annotation body.
 * 
 * @author piotrekhol
 * 
 */
public class ImportAnnotationModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = 8709783939660653237L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ImportAnnotationModal.class);

    /** The resource that is being annotated. */
    private IModel<? extends Annotable> annotableModel;

    /** Component for the uploaded file. */
    private FileUploadField fileUpload;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param annotableModel
     *            the annotated resource
     * @param eventBusModel
     *            bus model
     */
    public ImportAnnotationModal(String id, IModel<? extends Annotable> annotableModel,
            final IModel<EventBus> eventBusModel) {
        super(id, eventBusModel, "import-annotation-modal", "Import annotations");
        this.annotableModel = annotableModel;

        // Enable multipart mode (need for uploads file)
        form.setMultiPart(true);

        // max upload size, 10k
        form.setMaxSize(Bytes.megabytes(10));
        fileUpload = new FileUploadField("fileUpload");
        modal.add(fileUpload);
    }


    @Override
    public void onOk(AjaxRequestTarget target) {
        final FileUpload uploadedFile = fileUpload.getFileUpload();
        if (uploadedFile != null) {
            try {
                eventBusModel.getObject().post(new ImportAnnotationReadyEvent(target, annotableModel, uploadedFile));
                hide(target);
            } catch (Exception e) {
                LOG.error("Error when importing annotation", e);
                error(e);
            }
        }
        target.add(feedbackPanel);
    }
}
