package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.events.CancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.OkClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationReadyEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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
     * @param eventBusModel
     *            bus model
     */
    public ImportAnnotationModal(String id, final IModel<EventBus> eventBusModel) {
        super(id, eventBusModel, "import-annotation-modal", "Import annotations");

        // Enable multipart mode (need for uploads file)
        form.setMultiPart(true);

        // max upload size, 10k
        form.setMaxSize(Bytes.megabytes(10));
        fileUpload = new FileUploadField("fileUpload");
        form.add(fileUpload);
    }


    /**
     * Display this modal.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onImportAnnotationsClicked(ImportAnnotationClickedEvent event) {
        this.annotableModel = event.getAnnotableModel();
        show(event.getTarget());
    }


    /**
     * Post an event and hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onOk(OkClickedEvent event) {
        final FileUpload uploadedFile = fileUpload.getFileUpload();
        if (uploadedFile != null) {
            try {
                eventBusModel.getObject().post(
                    new ImportAnnotationReadyEvent(event.getTarget(), annotableModel, uploadedFile));
                hide(event.getTarget());
            } catch (Exception e) {
                LOG.error("Error when importing annotation", e);
                error(e);
            }
        }
        event.getTarget().add(feedbackPanel);
    }


    /**
     * Hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onCancel(CancelClickedEvent event) {
        hide(event.getTarget());
    }

}
