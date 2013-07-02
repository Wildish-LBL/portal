package pl.psnc.dl.wf4ever.portal.components.annotations;

import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.Annotation;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.RequiredURITextField;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationCancelledEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationDeletedEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.ApplyEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.CancelEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.DeleteEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.EditEvent;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTimestampModel;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;
import pl.psnc.dl.wf4ever.portal.model.LocalNameModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A panel for inline comments, show the annotation author and creation date.
 * 
 * @author piotrekhol
 * 
 */
public class EditableAnnotationTextPanel extends EventPanel {

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EditableAnnotationTextPanel.class);

    /** id. */
    private static final long serialVersionUID = 1L;

    /** A CSS file for this panel. */
    private static final JavaScriptResourceReference JS_REFERENCE = new JavaScriptResourceReference(
            AdvancedAnnotationsPanel.class, "EditableAnnotationTextPanel.js");

    /** The read only view of the field. */
    private Fragment viewFragment;

    /** The fragment that allows to edit the property and the value. */
    private EditFragment editFragment;

    /** The property that the user can edit. */
    private URI newProperty;

    /** The value that the user can edit. */
    private String newValue;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the model for the quad
     * @param eventBusModel
     *            event bus model for when a comment is added, deleted or edited
     * @param editMode
     *            should the field start in an edit mode
     */
    public EditableAnnotationTextPanel(String id, AnnotationTripleModel model, final IModel<EventBus> eventBusModel,
            boolean editMode) {
        super(id, model, eventBusModel);
        setOutputMarkupPlaceholderTag(true);
        LoadableDetachableModel<EventBus> internalEventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        internalEventBusModel.getObject().register(this);
        newProperty = model.getObject().getProperty();
        newValue = model.getObject().getValue();
        viewFragment = new ViewFragment("content", "view", this, model, internalEventBusModel);
        editFragment = new EditFragment("content", "editSingle", this, new PropertyModel<URI>(this, "newProperty"),
                new PropertyModel<String>(this, "newValue"), internalEventBusModel);
        add(editMode ? editFragment : viewFragment);
    }


    /**
     * Constructor for a version that is not associated with any particular annotation triple (for adding new ones).
     * 
     * @param id
     *            wicket id
     * @param annotable
     *            the resource that will be annotated
     * @param eventBusModel
     *            event bus model for when a comment is added, deleted or edited
     */
    public EditableAnnotationTextPanel(String id, Annotable annotable, final IModel<EventBus> eventBusModel) {
        this(id, new AnnotationTripleModel(new Model<>(annotable), (URI) null, false), eventBusModel, true);
    }


    /**
     * Called when the edit button is clicked. Replace the view fragment with edit.
     * 
     * @param event
     *            click event
     */
    @Subscribe
    public void onEdit(EditEvent event) {
        viewFragment.replaceWith(editFragment);
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
    }


    /**
     * Called when the delete button is clicked. Update the value and post another event.
     * 
     * @param event
     *            click event
     */
    @Subscribe
    public void onDelete(DeleteEvent event) {
        ((AnnotationTripleModel) this.getDefaultModel()).delete();
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
        if (eventBusModel != null && eventBusModel.getObject() != null) {
            IModel<? extends Annotable> annotable = ((AnnotationTripleModel) this.getDefaultModel())
                    .getAnnotableModel();
            eventBusModel.getObject().post(new AnnotationDeletedEvent(event.getTarget(), annotable));
        }
    }


    /**
     * Called when the apply button is clicked. Replace the edit fragment with view and post an event.
     * 
     * @param event
     *            click event
     */
    @Subscribe
    public void onApply(ApplyEvent event) {
        editFragment.replaceWith(viewFragment);
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
        //the tripleCopy now holds the updated property and value
        ((AnnotationTripleModel) this.getDefaultModel()).setPropertyAndValue(newProperty, newValue);
        //post event
        if (eventBusModel != null && eventBusModel.getObject() != null) {
            IModel<? extends Annotable> annotable = ((AnnotationTripleModel) this.getDefaultModel())
                    .getAnnotableModel();
            eventBusModel.getObject().post(new AnnotationAddedEvent(event.getTarget(), annotable));
        }
    }


    /**
     * Called when the cancel button is clicked. Replace the edit fragment with view and post an event.
     * 
     * @param event
     *            click event
     */
    @Subscribe
    public void onCancel(CancelEvent event) {
        editFragment.replaceWith(viewFragment);
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
        if (eventBusModel != null && eventBusModel.getObject() != null) {
            IModel<? extends Annotable> annotable = ((AnnotationTripleModel) this.getDefaultModel())
                    .getAnnotableModel();
            eventBusModel.getObject().post(new AnnotationCancelledEvent(event.getTarget(), annotable));
        }
    }


    /**
     * Read-only view fragment.
     * 
     * @author piotrekhol
     * 
     */
    protected class ViewFragment extends Fragment {

        /** id. */
        private static final long serialVersionUID = -4169842101720666349L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param markupId
         *            fragment wicket id
         * @param markupProvider
         *            container defining the fragment
         * @param model
         *            value model
         * @param internalEventBusModel
         *            event bus model for button clicks
         */
        public ViewFragment(String id, String markupId, MarkupContainer markupProvider, IModel<AnnotationTriple> model,
                final IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);

            WebMarkupContainer propColumn = new WebMarkupContainer("property");
            propColumn.add(AttributeAppender.replace("data-original-title", new PropertyModel<>(model, "property")));
            propColumn.add(new Label("property-name", new LocalNameModel(new PropertyModel<URI>(model, "property"))));
            add(propColumn);

            WebMarkupContainer valueColumn = new WebMarkupContainer("value");
            valueColumn.add(AttributeAppender.replace("data-original-title", new AnnotationTimestampModel(
                    new PropertyModel<Annotation>(model, "annotation"))));
            valueColumn.add(new Label("value", new PropertyModel<String>(model, "value")));
            valueColumn.add(new AuthenticatedAjaxEventButton("edit", null, internalEventBusModel, EditEvent.class));
            valueColumn.add(new AuthenticatedAjaxEventButton("delete", null, internalEventBusModel, DeleteEvent.class));
            add(valueColumn);
        }

    }


    /**
     * A fragment which the user can use to edit the value.
     * 
     * @author piotrekhol
     * 
     */
    class EditFragment extends Fragment {

        /** id. */
        private static final long serialVersionUID = -4169842101720666349L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param markupId
         *            fragment wicket id
         * @param markupProvider
         *            container defining the fragment
         * @param propertyModel
         *            the property to edit
         * @param valueModel
         *            the value to edit
         * @param internalEventBusModel
         *            event bus model for button clicks
         */
        public EditFragment(String id, String markupId, MarkupContainer markupProvider, IModel<URI> propertyModel,
                IModel<String> valueModel, final IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider);
            setOutputMarkupPlaceholderTag(true);
            add(new RequiredURITextField("property-name", propertyModel));
            add(new TextField<>("value", valueModel));
            add(new AuthenticatedAjaxEventButton("apply", null, internalEventBusModel, ApplyEvent.class));
            add(new AuthenticatedAjaxEventButton("cancel", null, internalEventBusModel, CancelEvent.class)
                    .setDefaultFormProcessing(false));
        }


        @Override
        public void renderHead(IHeaderResponse response) {
            response.renderJavaScriptReference(JS_REFERENCE);
            super.renderHead(response);
        }
    }

}
