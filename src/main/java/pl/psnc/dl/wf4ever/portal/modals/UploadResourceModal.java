package pl.psnc.dl.wf4ever.portal.modals;

import java.net.URI;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.RequiredURITextField;
import pl.psnc.dl.wf4ever.portal.events.CancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.OkClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.model.ResourceClass;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
public class UploadResourceModal extends EventPanel {

    /** Type of currently added resource. */
    private ResourceType resourceType = ResourceType.LOCAL;

    /** Resource class. */
    private ResourceClass resourceClass = null;

    /** Resource URI. */
    private URI resourceURI;

    /** Div with remote resource URI. */
    private final WebMarkupContainer resourceDiv;

    /** Div for uploading files. */
    private final WebMarkupContainer fileDiv;

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;

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
        super(id, null, eventBusModel);
        Form<?> form = new Form<Void>("uploadResourceForm");
        add(form);

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        LoadableDetachableModel<EventBus> internalEventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        internalEventBusModel.getObject().register(this);

        // Enable multipart mode (need for uploads file)
        form.setMultiPart(true);

        resourceDiv = new WebMarkupContainer("resourceURIDiv");
        resourceDiv.setOutputMarkupId(true);
        resourceDiv.setOutputMarkupPlaceholderTag(true);
        form.add(resourceDiv);
        fileDiv = new WebMarkupContainer("fileUploadDiv");
        fileDiv.setOutputMarkupId(true);
        fileDiv.setOutputMarkupPlaceholderTag(true);
        form.add(fileDiv);

        DropDownChoice<ResourceClass> resourceClassDropDown = new DropDownChoice<>("typeList",
                new PropertyModel<ResourceClass>(this, "resourceClass"), ResourceClass.RESOURCE_CLASSES,
                new ChoiceRenderer<ResourceClass>("name", "uri"));
        resourceClassDropDown.setNullValid(true);
        form.add(resourceClassDropDown);

        RadioGroup<ResourceType> radioGroup = new RadioGroup<ResourceType>("radioGroup",
                new PropertyModel<ResourceType>(this, "resourceType"));
        form.add(radioGroup);
        Radio<ResourceType> local = new Radio<ResourceType>("local", new Model<ResourceType>(ResourceType.LOCAL));
        local.add(new AjaxEventBehavior("onclick") {

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                resourceDiv.setVisible(false);
                fileDiv.setVisible(true);
                target.add(resourceDiv);
                target.add(fileDiv);
            }

        });
        radioGroup.add(local);
        Radio<ResourceType> remote = new Radio<ResourceType>("remote", new Model<ResourceType>(ResourceType.REMOTE));
        remote.add(new AjaxEventBehavior("onclick") {

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

        TextField<URI> resourceURIField = new RequiredURITextField("resourceURI", new PropertyModel<URI>(this,
                "resourceURI"));
        resourceDiv.add(resourceURIField);

        form.add(new AjaxEventButton("ok", form, internalEventBusModel, OkClickedEvent.class));
        form.add(new AjaxEventButton("cancel", form, internalEventBusModel, CancelClickedEvent.class)
                .setDefaultFormProcessing(false));
        form.add(new AjaxEventButton("close", form, internalEventBusModel, CancelClickedEvent.class)
                .setDefaultFormProcessing(false));
    }


    /**
     * Show itself.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAddResourceClicked(ResourceAddClickedEvent event) {
        event.getTarget().appendJavaScript("$('#upload-resource-modal').modal('show')");
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
                    event.getTarget().appendJavaScript("$('#upload-resource-modal').modal('hide')");
                }
                break;
            case REMOTE:
                eventBusModel.getObject()
                        .post(new ResourceAddReadyEvent(event.getTarget(), resourceURI, resourceClass));
                event.getTarget().appendJavaScript("$('#upload-resource-modal').modal('hide')");
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
        event.getTarget().appendJavaScript("$('#upload-resource-modal').modal('hide')");
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


    public ResourceType getResourceType() {
        return resourceType;
    }


    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }


    public ResourceClass getResourceClass() {
        return resourceClass;
    }


    public void setResourceClass(ResourceClass resourceClass) {
        this.resourceClass = resourceClass;
    }
}
