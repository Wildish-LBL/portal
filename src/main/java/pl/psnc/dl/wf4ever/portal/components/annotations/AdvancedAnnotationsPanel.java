package pl.psnc.dl.wf4ever.portal.components.annotations;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;

import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AbstractAnnotationEditedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AddAnnotationClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationClickedEvent;
import pl.psnc.dl.wf4ever.portal.model.wicket.AnnotationTripleModel;

/**
 * A panel aggregating action buttons for a folder - add a new folder or resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class AdvancedAnnotationsPanel extends Panel {

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
            item.add(new EditableAnnotationTextPanel("editable-annotation-triple", new AnnotationTripleModel(item
                    .getModelObject()), false));
            item.setRenderBodyOnly(true);
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

    /** The new annotation row. */
    private Component addAnnotationPanel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param basicPanelId
     *            id of the basic view panel (without any #)
     * @param model
     *            selected resource
     */
    public AdvancedAnnotationsPanel(String id, String basicPanelId, final IModel<? extends Annotable> model) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);
        form = new Form<Void>("form");
        add(form);
        Button backButton = new Button("show-basic");
        backButton.add(AttributeAppender.replace("data-target", "#" + basicPanelId));

        form.add(backButton);
        form.add(new AnnotationEditAjaxEventButton("import-annotations", form, model, null,
                ImportAnnotationClickedEvent.class));
        form.add(new AnnotationEditAjaxEventButton("annotate", form, model, this, AddAnnotationClickedEvent.class));

        form.add(new AnnotationTripleList("annotation-triple", new PropertyModel<List<AnnotationTriple>>(model,
                "annotationTriples")));

        addAnnotationPanel = new WebMarkupContainer("new-annotation");
        addAnnotationPanel.setOutputMarkupPlaceholderTag(true);
        addAnnotationPanel.setVisible(false);
        form.add(addAnnotationPanel);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS_REFERENCE));
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setEnabled(getDefaultModelObject() != null);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof ResourceSelectedEvent) {
            onResourceSelected((ResourceSelectedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof AbstractAnnotationEditedEvent) {
            onAnnotationEdited((AbstractAnnotationEditedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof AddAnnotationClickedEvent) {
            onAnnotateClicked((AddAnnotationClickedEvent) event.getPayload());
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
     * Refresh the panel when an annotation changes.
     * 
     * @param event
     *            AJAX event
     */
    private void onAnnotationEdited(AbstractAnnotationEditedEvent event) {
        addAnnotationPanel.setVisible(false);
        event.getTarget().add(this);
    }


    /**
     * Show the new annotation panel.
     * 
     * @param event
     *            the event that triggers this action
     */
    private void onAnnotateClicked(AddAnnotationClickedEvent event) {
        if (event.getAnnotableModel().getObject() == this.getDefaultModelObject()) {
            EditableAnnotationTextPanel panel = new EditableAnnotationTextPanel("new-annotation",
                    (Annotable) this.getDefaultModelObject());
            addAnnotationPanel.replaceWith(panel);
            addAnnotationPanel = panel;
            event.getTarget().add(addAnnotationPanel);
        }
    }

}
