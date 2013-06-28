package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.EditableTextPanel;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AbstractAnnotationEditedEvent;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * A panel for the basic metadata of a selected resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ResourceSummaryPanel extends EventPanel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ResourceSummaryPanel.class);


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            selected resource model
     * @param eventBusModel
     *            event bus model
     */
    public ResourceSummaryPanel(String id, IModel<Resource> model, IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupPlaceholderTag(true);

        EditableTextPanel titlePanel = new EditableTextPanel("titlePanel", new AnnotationTripleModel(model,
                URI.create(DCTerms.title.getURI()), true), eventBusModel, false);
        titlePanel.setCanDelete(false);
        EditableTextPanel descriptionPanel = new EditableTextPanel("descriptionPanel", new AnnotationTripleModel(
                model, URI.create(DCTerms.description.getURI()), true), eventBusModel, true);
        descriptionPanel.setCanDelete(false);

        add(new ExternalLink("uri", new PropertyModel<String>(model, "uri.toString"), new PropertyModel<URI>(model,
                "uri")));
        add(titlePanel);
        add(new Label("author", new PropertyModel<String>(model, "author.name")));
        add(new Label("createdFormatted", new PropertyModel<String>(model, "createdFormatted")));
        add(new Label("annotations", new PropertyModel<Integer>(model, "annotations.size")));
        add(descriptionPanel);
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getDefaultModelObject() != null);
    };


    /**
     * Refresh the panel when the selected resource changes.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onResourceSelected(ResourceSelectedEvent event) {
        event.getTarget().add(this);
    }


    /**
     * Refresh the panel when an annotation of this resource is updated.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAnnotationEdited(AbstractAnnotationEditedEvent event) {
        event.getTarget().add(this);
    }

}
