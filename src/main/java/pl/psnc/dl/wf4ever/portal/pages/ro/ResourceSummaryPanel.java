package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.components.WorkflowTransformPanel;
import pl.psnc.dl.wf4ever.portal.components.annotations.ResourceTypePanel;
import pl.psnc.dl.wf4ever.portal.components.form.EditableTextPanel;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AbstractAnnotationEditedEvent;
import pl.psnc.dl.wf4ever.portal.model.wicket.AnnotationTripleModel;
import pl.psnc.dl.wf4ever.portal.model.wicket.ResourceTypeModel;

import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * A panel for the basic metadata of a selected resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ResourceSummaryPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ResourceSummaryPanel.class);

    /** Information that the resource is a nested RO. */
    private WebMarkupContainer nestedRO;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            selected resource model
     * @param currentFolderModel
     *            current folder model
     */
    public ResourceSummaryPanel(String id, IModel<Resource> model, IModel<Folder> currentFolderModel) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);

        nestedRO = new WebMarkupContainer("nested-ro");
        nestedRO.setOutputMarkupPlaceholderTag(true);
        add(nestedRO);

        add(new ExternalLink("uri", new PropertyModel<String>(model, "uri.toString"), new PropertyModel<URI>(model,
                "uri")));
        add(new EditableTextPanel("titlePanel", new AnnotationTripleModel(model, URI.create(DCTerms.title.getURI()),
                true, false), false).setCanDelete(false));
        ResourceTypeModel resourceTypeModel = new ResourceTypeModel(model);
        add(new ResourceTypePanel("resource-type", resourceTypeModel));
        add(new WorkflowTransformPanel("transform", model, resourceTypeModel, currentFolderModel));
        add(new Label("author", new PropertyModel<String>(model, "author.name")));
        add(new Label("createdFormatted", new PropertyModel<String>(model, "createdFormatted")));
        add(new Label("annotations", new PropertyModel<Integer>(model, "annotations.size")));
        add(new EditableTextPanel("descriptionPanel", new AnnotationTripleModel(model, URI.create(DCTerms.description
                .getURI()), true, false), true).setCanDelete(false));
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getDefaultModelObject() != null);
        nestedRO.setVisible(getDefaultModelObject() != null && ((Resource) getDefaultModelObject()).isNestedRO());
    };


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof ResourceSelectedEvent) {
            onResourceSelected((ResourceSelectedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof AbstractAnnotationEditedEvent) {
            onAnnotationEdited((AbstractAnnotationEditedEvent) event.getPayload());
        }
    }


    /**
     * Refresh the panel when the selected resource changes.
     * 
     * @param event
     *            AJAX event
     */
    private void onResourceSelected(ResourceSelectedEvent event) {
        event.getTarget().add(this);
    }


    /**
     * Refresh the panel when an annotation of this resource is updated.
     * 
     * @param event
     *            AJAX event
     */
    private void onAnnotationEdited(AbstractAnnotationEditedEvent event) {
        event.getTarget().add(this);
    }

}
