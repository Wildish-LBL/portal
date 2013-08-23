package pl.psnc.dl.wf4ever.portal.modals;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.components.annotations.ResourceTypeDropDownChoice;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.model.ResourceLocalRemote;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;

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

    /**
     * the resource type that is currently selected. This is not automatically sent to the model, only after the user
     * clicks OK.
     */
    private Set<ResourceType> resourceTypes = new HashSet<>();

    /** Resource URI. */
    private URI resourceURI;

    /** Div with remote resource URI. */
    private final WebMarkupContainer resourceDiv;

    /** Div for uploading files. */
    private final WebMarkupContainer fileDiv;

    /** Component for the uploaded file. */
    private FileUploadField fileUpload;

    /** Is the uploaded file an RO bundle? */
    private boolean roBundle;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public UploadResourceModal(String id) {
        super(id, "upload-resource-modal", "Upload a resource");

        // Enable multipart mode (need for uploads file)
        form.setMultiPart(true);

        resourceDiv = new WebMarkupContainer("resourceURIDiv") {

            /** id. */
            private static final long serialVersionUID = -5439904589164043855L;


            @Override
            protected void onConfigure() {
                setVisible(resourceType == ResourceLocalRemote.REMOTE);
            }
        };
        resourceDiv.setOutputMarkupPlaceholderTag(true);
        modal.add(resourceDiv);
        fileDiv = new WebMarkupContainer("fileUploadDiv") {

            /** id. */
            private static final long serialVersionUID = -481041541365628716L;


            @Override
            protected void onConfigure() {
                setVisible(resourceType == ResourceLocalRemote.LOCAL);
            }
        };
        fileDiv.setOutputMarkupPlaceholderTag(true);
        modal.add(fileDiv);
        modal.add(new ResourceTypeDropDownChoice("typeList",
                new PropertyModel<Set<ResourceType>>(this, "resourceTypes")));

        RadioGroup<ResourceLocalRemote> radioGroup = new RadioGroup<ResourceLocalRemote>("radioGroup",
                new PropertyModel<ResourceLocalRemote>(this, "resourceType"));
        modal.add(withFocus(radioGroup));
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            /** id. */
            private static final long serialVersionUID = -1653173329010286091L;


            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(resourceDiv);
                target.add(fileDiv);
            }

        });
        radioGroup.add(new Radio<ResourceLocalRemote>("local",
                new Model<ResourceLocalRemote>(ResourceLocalRemote.LOCAL)));
        radioGroup.add(new Radio<ResourceLocalRemote>("remote", new Model<ResourceLocalRemote>(
                ResourceLocalRemote.REMOTE)));

        // max upload size, 10k
        form.setMaxSize(Bytes.megabytes(10));
        fileUpload = new FileUploadField("fileUpload");
        fileDiv.add(fileUpload);

        fileDiv.add(new CheckBox("ro-bundle-checkbox", new PropertyModel<Boolean>(this, "roBundle")));

        resourceDiv.add(new RequiredTextField<URI>("resourceURI", new PropertyModel<URI>(this, "resourceURI")));
    }


    @Override
    public void onOk(AjaxRequestTarget target) {
        switch (resourceType) {
            case LOCAL:
            default:
                final FileUpload uploadedFile = fileUpload.getFileUpload();
                if (uploadedFile != null) {
                    String mimeType = roBundle ? "application/vnd.wf4ever.robundle+zip" : null;
                    send(getPage(), Broadcast.BREADTH, new ResourceAddReadyEvent(target, uploadedFile, resourceTypes,
                            mimeType));
                    hide(target);
                }
                break;
            case REMOTE:
                send(getPage(), Broadcast.BREADTH, new ResourceAddReadyEvent(target, resourceURI, resourceTypes));
                hide(target);
                break;
        }
        target.add(feedbackPanel);
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


    public Set<ResourceType> getResourceTypes() {
        return resourceTypes;
    }


    public void setResourceTypes(Set<ResourceType> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }


    public boolean isRoBundle() {
        return roBundle;
    }


    public void setRoBundle(boolean roBundle) {
        this.roBundle = roBundle;
    }
}
