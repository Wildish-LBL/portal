package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.RequiredURITextField;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
class UploadResourceModal extends Panel {

    /** Type of resource. */
    private enum ResourceType {
        /** A file uploaded by the user. */
        LOCAL,
        /** A reference to an external resource. */
        REMOTE
    }


    /** Type of currently added resource. */
    private ResourceType resourceType = ResourceType.LOCAL;

    /** Resource URI. */
    private URI resourceURI;

    /** Div with remote resource URI. */
    private final WebMarkupContainer resourceDiv;

    /** Div for uploading files. */
    private final WebMarkupContainer fileDiv;

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param roPage
     *            owning page
     * @param set
     *            set of resource groups to choose from
     */
    public UploadResourceModal(String id, final RoPage roPage) {
        super(id);
        Form<?> form = new Form<Void>("uploadResourceForm");
        add(form);

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

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
        final FileUploadField fileUpload = new FileUploadField("fileUpload");
        fileDiv.add(fileUpload);

        TextField<URI> resourceURIField = new RequiredURITextField("resourceURI", new PropertyModel<URI>(this,
                "resourceURI"));
        resourceDiv.add(resourceURIField);

        form.add(new MyAjaxButton("confirmUploadResource", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                switch (resourceType) {
                    case LOCAL:
                    default:
                        final FileUpload uploadedFile = fileUpload.getFileUpload();
                        if (uploadedFile != null) {
                            try {
                                roPage.onResourceAdd(target, uploadedFile);
                                target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
                            } catch (IOException | ROSRSException | ROException e) {
                                error(e);
                            }
                        }
                        break;
                    case REMOTE:
                        try {
                            roPage.onRemoteResourceAdded(target, resourceURI);
                            target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
                        } catch (ROSRSException | ROException e) {
                            error(e);
                        }
                        break;
                }
                target.add(feedbackPanel);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(feedbackPanel);
            }

        });
        form.add(new MyAjaxButton("cancelUploadResource", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
            }
        }.setDefaultFormProcessing(false));
    }


    @Override
    protected void onConfigure() {
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
}
