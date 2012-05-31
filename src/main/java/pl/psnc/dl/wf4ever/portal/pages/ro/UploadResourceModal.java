package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.URIConverter;

@SuppressWarnings("serial")
class UploadResourceModal extends Panel {

    private enum ResourceType {
        LOCAL,
        REMOTE
    }


    private ResourceType resourceType = ResourceType.LOCAL;

    private URI resourceURI;

    private URI downloadURI;

    private final WebMarkupContainer resourceDiv;

    private final WebMarkupContainer downloadDiv;

    private final WebMarkupContainer fileDiv;

    private MyFeedbackPanel feedbackPanel;


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

        final TextField<URI> resourceURIField = new RequiredTextField<URI>("resourceURI", new PropertyModel<URI>(this,
                "resourceURI"), URI.class) {

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new URIConverter();
            }
        };
        resourceDiv.add(resourceURIField);

        final TextField<URI> downloadURIField = new TextField<URI>("downloadURI", new PropertyModel<URI>(this,
                "downloadURI"), URI.class) {

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new URIConverter();
            }
        };
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
                        } catch (URISyntaxException | IOException e) {
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
