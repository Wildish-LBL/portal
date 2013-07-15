package pl.psnc.dl.wf4ever.portal.modals;

import java.net.URI;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.components.annotations.ResourceTypeDropDownChoice;
import pl.psnc.dl.wf4ever.portal.components.form.RequiredURITextField;
import pl.psnc.dl.wf4ever.portal.events.CancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.OkClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.model.ResourceLocalRemote;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
public class UploadResourceModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = -7754788822535330561L;

    /** Type of currently added resource. */
    private ResourceLocalRemote resourceType = ResourceLocalRemote.LOCAL;

    /** Resource class. */
    private ResourceType resourceClass = null;

    /** Resource URI. */
    private URI resourceURI;

    /** Div with remote resource URI. */
    private final WebMarkupContainer resourceDiv;

    /** Div for uploading files. */
    private final WebMarkupContainer fileDiv;

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
    public UploadResourceModal(String id, final IModel<EventBus> eventBusModel) {
        super(id, eventBusModel, "upload-resource-modal", "Upload a resource");

        // Enable multipart mode (need for uploads file)
        form.setMultiPart(true);

        resourceDiv = new WebMarkupContainer("resourceURIDiv");
        resourceDiv.setOutputMarkupId(true);
        resourceDiv.setOutputMarkupPlaceholderTag(true);
        modal.add(resourceDiv);
        fileDiv = new WebMarkupContainer("fileUploadDiv");
        fileDiv.setOutputMarkupId(true);
        fileDiv.setOutputMarkupPlaceholderTag(true);
        modal.add(fileDiv);
        modal.add(new ResourceTypeDropDownChoice("typeList", new PropertyModel<ResourceType>(this, "resourceClass")));

        RadioGroup<ResourceLocalRemote> radioGroup = new RadioGroup<ResourceLocalRemote>("radioGroup",
                new PropertyModel<ResourceLocalRemote>(this, "resourceType"));
        modal.add(radioGroup);
        Radio<ResourceLocalRemote> local = new Radio<ResourceLocalRemote>("local", new Model<ResourceLocalRemote>(
                ResourceLocalRemote.LOCAL));
        local.add(new AjaxEventBehavior("onclick") {

            /** id. */
            private static final long serialVersionUID = -1653173329010286091L;


            @Override
            protected void onEvent(AjaxRequestTarget target) {
                resourceDiv.setVisible(false);
                fileDiv.setVisible(true);
                target.add(resourceDiv);
                target.add(fileDiv);
            }

        });
        radioGroup.add(local);
        Radio<ResourceLocalRemote> remote = new Radio<ResourceLocalRemote>("remote", new Model<ResourceLocalRemote>(
                ResourceLocalRemote.REMOTE));
        remote.add(new AjaxEventBehavior("onclick") {

            /** id. */
            private static final long serialVersionUID = -1689888759359590693L;


            @Override
            protected void onEvent(AjaxRequestTarget target) {
                resourceDiv.setVisible(true);
                fileDiv.setVisible(false);
                target.add(resourceDiv);
                target.add(fileDiv);
            }

        });
        radioGroup.add(remote);

        // max upload size, 10k
        form.setMaxSize(Bytes.megabytes(10));
        fileUpload = new FileUploadField("fileUpload");
        fileDiv.add(fileUpload);

        resourceDiv.add(new RequiredURITextField("resourceURI", new PropertyModel<URI>(this, "resourceURI")));
    }


    /**
     * Show itself.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAddResourceClicked(ResourceAddClickedEvent event) {
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
        switch (resourceType) {
            case LOCAL:
            default:
                final FileUpload uploadedFile = fileUpload.getFileUpload();
                if (uploadedFile != null) {
                    eventBusModel.getObject().post(
                        new ResourceAddReadyEvent(event.getTarget(), uploadedFile, resourceClass));
                    hide(event.getTarget());
                }
                break;
            case REMOTE:
                eventBusModel.getObject()
                        .post(new ResourceAddReadyEvent(event.getTarget(), resourceURI, resourceClass));
                hide(event.getTarget());
                break;
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


    @Override
    protected void onConfigure() {
        super.onConfigure();
        switch (resourceType) {
            case LOCAL:
                resourceDiv.setVisible(false);
                fileDiv.setVisible(true);
                break;
            case REMOTE:
                resourceDiv.setVisible(true);
                fileDiv.setVisible(false);
                break;
            default:
                break;
        }
    }


    public URI getResourceURI() {
        return resourceURI;
    }


    public void setResourceURI(URI resourceURI) {
        this.resourceURI = resourceURI;
    }


    public ResourceLocalRemote getResourceType() {
        return resourceType;
    }


    public void setResourceType(ResourceLocalRemote resourceType) {
        this.resourceType = resourceType;
    }


    public ResourceType getResourceClass() {
        return resourceClass;
    }


    public void setResourceClass(ResourceType resourceClass) {
        this.resourceClass = resourceClass;
    }
}
