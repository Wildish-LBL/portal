package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
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

    /** Download URI, currently not used. */
    private URI downloadURI;

    /** Div with remote resource URI. */
    private final WebMarkupContainer resourceDiv;

    /** Div with remote resource download URI. */
    private final WebMarkupContainer downloadDiv;

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
    public UploadResourceModal(String id, final RoPage roPage, final Set<ResourceGroup> set) {
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
        downloadDiv = new WebMarkupContainer("downloadURIDiv");
        downloadDiv.setOutputMarkupId(true);
        downloadDiv.setOutputMarkupPlaceholderTag(true);
        form.add(downloadDiv);
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
                downloadDiv.setVisible(false);
                fileDiv.setVisible(true);
                target.add(resourceDiv);
                target.add(downloadDiv);
                target.add(fileDiv);
            }

        });
        radioGroup.add(local);
        Radio<ResourceType> remote = new Radio<ResourceType>("remote", new Model<ResourceType>(ResourceType.REMOTE));
        remote.add(new AjaxEventBehavior("onclick") {

            @Override
            protected void onEvent(AjaxRequestTarget target) {
                resourceDiv.setVisible(true);
                downloadDiv.setVisible(true);
                fileDiv.setVisible(false);
                target.add(resourceDiv);
                target.add(downloadDiv);
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

        TextField<URI> downloadURIField = new RequiredURITextField("downloadURI", new PropertyModel<URI>(this,
                "downloadURI"));
        downloadDiv.add(downloadURIField);

        List<ResourceGroup> types = new ArrayList<>(set);

        final Set<ResourceGroup> selectedTypes = new HashSet<>();
        CheckGroup<ResourceGroup> group = new CheckGroup<ResourceGroup>("group", selectedTypes);
        form.add(group);
        ListView<ResourceGroup> list = new ListView<ResourceGroup>("resourceTypes", types) {

            @Override
            protected void populateItem(ListItem<ResourceGroup> item) {
                Check<ResourceGroup> check = new Check<ResourceGroup>("checkbox", item.getModel());
                item.add(check);
                item.add(new AttributeModifier("for", new Model<String>(check.getMarkupId())));
                item.setEscapeModelStrings(false);
                item.add(new Label("title", item.getModelObject().getTitle()));
            }
        };
        list.setReuseItems(true);
        group.add(list);

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
                                roPage.onResourceAdd(target, uploadedFile, selectedTypes);
                                target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
                            } catch (IOException e) {
                                error(e);
                            }
                        }
                        break;
                    case REMOTE:
                        try {
                            roPage.onRemoteResourceAdded(target, resourceURI, downloadURI, selectedTypes);
                            target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
                        } catch (IOException e) {
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
                downloadDiv.setVisible(false);
                fileDiv.setVisible(true);
                break;
            case REMOTE:
                resourceDiv.setVisible(true);
                downloadDiv.setVisible(true);
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


    public URI getDownloadURI() {
        return downloadURI;
    }


    public void setDownloadURI(URI downloadURI) {
        this.downloadURI = downloadURI;
    }


    public ResourceType getResourceType() {
        return resourceType;
    }


    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}
