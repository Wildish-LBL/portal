package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.LoadingCircle;
import pl.psnc.dl.wf4ever.portal.components.form.EditableTextPanel;
import pl.psnc.dl.wf4ever.portal.events.RoLoadedEvent;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Aggregated resource panel.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class RoSummaryPanel extends EventPanel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RoSummaryPanel.class);

    /** Temporary panel while data are loading. */
    private WebMarkupContainer tmp;


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
    public RoSummaryPanel(String id, IModel<ResearchObject> model, IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupId(true);

        EditableTextPanel titlePanel = new EditableTextPanel("titlePanel", new AnnotationTripleModel(
                AnnotationTripleModel.ANY_ANNOTATION, model, URI.create(DCTerms.title.getURI())), eventBusModel, false);
        titlePanel.setCanDelete(false);
        EditableTextPanel descriptionPanel = new EditableTextPanel("descriptionPanel", new AnnotationTripleModel(
                AnnotationTripleModel.ANY_ANNOTATION, model, URI.create(DCTerms.description.getURI())), eventBusModel,
                true);
        descriptionPanel.setCanDelete(false);

        add(new ExternalLink("uri", new PropertyModel<String>(model, "uri.toString"), new PropertyModel<URI>(model,
                "uri")));
        add(titlePanel);
        add(new Label("author", new PropertyModel<String>(model, "author.name")));
        add(new Label("createdFormatted", new PropertyModel<String>(model, "createdFormatted")));
        add(new Label("evoType.toString", new PropertyModel<String>(model, "evoType.toString")));
        add(new Label("resources", new PropertyModel<Integer>(model, "resourcesWithoutFolders.size")));
        add(new Label("annotations", new PropertyModel<Integer>(model, "annotations.size")));
        add(descriptionPanel);
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (!((ResearchObject) getDefaultModelObject()).isLoaded()) {
            tmp = new LoadingCircle(getId(), "Loading...");
            this.replaceWith(tmp);
        }
    }


    /**
     * Replace the temporary panel with this when the metadata has loaded.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onRoLoaded(RoLoadedEvent event) {
        if (tmp != null) {
            tmp.replaceWith(this);
            tmp = null;
        }
        event.getTarget().add(this);
    }
}
