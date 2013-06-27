package pl.psnc.dl.wf4ever.portal.components.annotations;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AbstractAnnotationEditedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotateEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationClickedEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A panel aggregating action buttons for a folder - add a new folder or resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class AdvancedAnnotationsPanel extends EventPanel {

    /**
     * A list of annotation triples.
     * 
     * @author piotrekhol
     * 
     */
    final class AnnotationTripleList extends ListView<AnnotationTriple> {

        /** id. */
        private static final long serialVersionUID = -3620346062427280309L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param model
         *            list of annotation triples
         */
        public AnnotationTripleList(String id, IModel<? extends List<? extends AnnotationTriple>> model) {
            super(id, model);
        }


        @Override
        protected void populateItem(ListItem<AnnotationTriple> item) {
            item.add(new EditableAnnotationTextPanel("editable-annotation-triple", item.getModel(),
                    internalEventBusModel).setRenderBodyOnly(true));
        }

    }


    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(AdvancedAnnotationsPanel.class);

    /** A CSS file for this panel. */
    private static final CssResourceReference CSS_REFERENCE = new CssResourceReference(AdvancedAnnotationsPanel.class,
            "AdvancedAnnotationsPanel.css");

    /** A form for the buttons. */
    private Form<Void> form;

    /** Internal event bus for edit clicks. */
    private IModel<EventBus> internalEventBusModel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param basicPanelId
     *            id of the basic view panel (without any #)
     * @param model
     *            selected resource
     * @param eventBusModel
     *            event bus model for button clicks
     */
    public AdvancedAnnotationsPanel(String id, String basicPanelId, final IModel<? extends Annotable> model,
            final IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupPlaceholderTag(true);
        form = new Form<Void>("form");
        add(form);
        Button backButton = new Button("show-basic");
        backButton.add(AttributeAppender.replace("data-target", "#" + basicPanelId));

        internalEventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        internalEventBusModel.getObject().register(this);

        form.add(backButton);
        form.add(new AnnotationEditAjaxEventButton("import-annotations", form, model, eventBusModel,
                ImportAnnotationClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("annotate", form, eventBusModel, AnnotateEvent.class));

        form.add(new AnnotationTripleList("annotation-triple", new PropertyModel<List<AnnotationTriple>>(model,
                "annotationTriples")));
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS_REFERENCE);
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setEnabled(getDefaultModelObject() != null);
    }


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
     * Refresh the panel when an annotation changes.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAnnotationEdited(AbstractAnnotationEditedEvent event) {
        event.getTarget().add(this);
    }


    @Subscribe
    public void onAnnotateClicked(AnnotateEvent event) {
        //TODO
        System.out.println("annotate");
    }

}
