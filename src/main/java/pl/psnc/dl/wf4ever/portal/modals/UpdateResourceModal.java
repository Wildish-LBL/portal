package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceUpdateReadyEvent;

import com.google.common.eventbus.EventBus;

/**
 * A modal for updating resources that are already aggregated in the RO.
 * 
 * @author piotrekhol
 * 
 */
public class UpdateResourceModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = -7754788822535330561L;

    /** Component for the uploaded file. */
    private FileUploadField fileUpload;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param eventBusModel
     *            event bus
     */
    public UpdateResourceModal(String id, final IModel<EventBus> eventBusModel) {
        super(id, eventBusModel, "update-resource-modal", "Upload a resource");

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
            eventBusModel.getObject().post(new ResourceUpdateReadyEvent(target, uploadedFile));
            hide(target);
        }
        target.add(feedbackPanel);
    }

}
