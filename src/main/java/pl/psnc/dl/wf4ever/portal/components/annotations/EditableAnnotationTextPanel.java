package pl.psnc.dl.wf4ever.portal.components.annotations;

import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.Annotation;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.Utils;

import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.ProtectedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.ErrorEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationCancelledEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationDeletedEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.ApplyEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.CancelEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.DeleteEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.EditEvent;
import pl.psnc.dl.wf4ever.portal.model.wicket.AnnotationTimestampModel;
import pl.psnc.dl.wf4ever.portal.model.wicket.AnnotationTripleModel;
import pl.psnc.dl.wf4ever.portal.model.wicket.LocalNameModel;

/**
 * A panel for inline comments, show the annotation author and creation date.
 * 
 * @author piotrekhol
 * 
 */
public class EditableAnnotationTextPanel extends Panel {

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
     * @param editMode
     *            should the field start in an edit mode
     */
    public EditableAnnotationTextPanel(String id, AnnotationTripleModel model, boolean editMode) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);
        newProperty = model.getObject().getProperty();
        newValue = model.getObject().getValue();
        viewFragment = new ViewFragment("content", "view", this, model);
        editFragment = new EditFragment("content", "editSingle", this, new PropertyModel<URI>(this, "newProperty"),
                new PropertyModel<String>(this, "newValue"));
        add(editMode ? editFragment : viewFragment).setOutputMarkupPlaceholderTag(true);
    }


    /**
     * Constructor for a version that is not associated with any particular annotation triple (for adding new ones).
     * 
     * @param id
     *            wicket id
     * @param annotable
     *            the resource that will be annotated
     */
    public EditableAnnotationTextPanel(String id, Annotable annotable) {
        this(id, new AnnotationTripleModel(new Model<>(annotable), (URI) null, false), true);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(JS_REFERENCE));
        super.renderHead(response);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof EditEvent) {
            onEdit((EditEvent) event.getPayload());
        }
        if (event.getPayload() instanceof DeleteEvent) {
            onDelete((DeleteEvent) event.getPayload());
        }
        if (event.getPayload() instanceof ApplyEvent) {
            onApply((ApplyEvent) event.getPayload());
        }
        if (event.getPayload() instanceof CancelEvent) {
            onCancel((CancelEvent) event.getPayload());
        }
    }


    /**
     * Called when the edit button is clicked. Replace the view fragment with edit.
     * 
     * @param event
     *            click event
     */
    private void onEdit(EditEvent event) {
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
    private void onDelete(DeleteEvent event) {
        ((AnnotationTripleModel) this.getDefaultModel()).delete();
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
        IModel<? extends Annotable> annotable = ((AnnotationTripleModel) this.getDefaultModel()).getAnnotableModel();
        send(getPage(), Broadcast.BREADTH, new AnnotationDeletedEvent(event.getTarget(), annotable));
    }


    /**
     * Called when the apply button is clicked. Replace the edit fragment with view and post an event.
     * 
     * @param event
     *            click event
     */
    private void onApply(ApplyEvent event) {
        editFragment.replaceWith(viewFragment);
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
        //the tripleCopy now holds the updated property and value
        ((AnnotationTripleModel) this.getDefaultModel()).setPropertyAndValue(newProperty, newValue);
        //post event
        IModel<? extends Annotable> annotable = ((AnnotationTripleModel) this.getDefaultModel()).getAnnotableModel();
        send(getPage(), Broadcast.BREADTH, new AnnotationAddedEvent(event.getTarget(), annotable));
    }


    /**
     * Called when the cancel button is clicked. Replace the edit fragment with view and post an event.
     * 
     * @param event
     *            click event
     */
    private void onCancel(CancelEvent event) {
        editFragment.replaceWith(viewFragment);
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
        IModel<? extends Annotable> annotable = ((AnnotationTripleModel) this.getDefaultModel()).getAnnotableModel();
        send(getPage(), Broadcast.BREADTH, new AnnotationCancelledEvent(event.getTarget(), annotable));
    }


    /**
     * Read-only view fragment.
     * 
     * @author piotrekhol
     * 
     */
    protected class ViewFragment extends Fragment {

        /**
         * External link for values that are URIs.
         * 
         * @author piotrekhol
         * 
         */
        private class LinkFragment extends Fragment {

            /** id. */
            private static final long serialVersionUID = -7246837915674720631L;


            /**
             * Constructor.
             * 
             * @param id
             *            wicket id where to place this fragment
             * @param model
             *            String model of the value/href
             */
            public LinkFragment(String id, IModel<String> model) {
                super(id, "link", ViewFragment.this, model);
                add(new ExternalLink("anchor", model, model));
            }
        }


        /** id. */
        private static final long serialVersionUID = -4169842101720666349L;

        /** The value. It may be a simple label of the {@link LinkFragment}. */
        private Component value;


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
         */
        public ViewFragment(String id, String markupId, MarkupContainer markupProvider, IModel<AnnotationTriple> model) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);

            WebMarkupContainer propColumn = new WebMarkupContainer("property");
            propColumn.add(AttributeAppender.replace("data-original-title", new PropertyModel<>(model, "property")));
            propColumn.add(new Label("property-name", new LocalNameModel(new PropertyModel<URI>(model, "property"))));
            add(propColumn);

            WebMarkupContainer valueColumn = new WebMarkupContainer("value");
            valueColumn.add(AttributeAppender.replace("data-original-title", new AnnotationTimestampModel(
                    new PropertyModel<Annotation>(model, "annotation"))));
            value = new Label("value", new PropertyModel<String>(model, "value"));
            valueColumn.add(value);
            valueColumn.add(new ProtectedAjaxEventButton("edit", null, EditableAnnotationTextPanel.this,
                    EditEvent.class));
            valueColumn.add(new ProtectedAjaxEventButton("delete", null, EditableAnnotationTextPanel.this,
                    DeleteEvent.class));
            add(valueColumn);
        }


        @SuppressWarnings("unchecked")
        @Override
        protected void onConfigure() {
            String text = value.getDefaultModelObjectAsString();
            Component replacementValue = null;
            if (Utils.isAbsoluteURI(text) && value instanceof Label) {
                replacementValue = new LinkFragment(value.getId(), (IModel<String>) value.getDefaultModel());
            } else if (value instanceof LinkFragment) {
                replacementValue = new Label(value.getId(), value.getDefaultModel());
            }
            if (replacementValue != null) {
                value.replaceWith(replacementValue);
                value = replacementValue;
            }
            super.onConfigure();
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

        /** A div that contains all components. */
        private WebMarkupContainer controlGroup;


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
         */
        public EditFragment(String id, String markupId, MarkupContainer markupProvider, IModel<URI> propertyModel,
                IModel<String> valueModel) {
            super(id, markupId, markupProvider);
            setOutputMarkupPlaceholderTag(true);
            controlGroup = new WebMarkupContainer("control-group");
            controlGroup.setOutputMarkupPlaceholderTag(true);
            add(controlGroup);
            controlGroup.add(new RequiredTextField<URI>("property-name", propertyModel));
            controlGroup.add(new TextField<>("value", valueModel));
            controlGroup.add(new ProtectedAjaxEventButton("apply", null, EditableAnnotationTextPanel.this,
                    ApplyEvent.class));
            controlGroup.add(new ProtectedAjaxEventButton("cancel", null, EditableAnnotationTextPanel.this,
                    CancelEvent.class).setDefaultFormProcessing(false));
            controlGroup.add(new MyFeedbackPanel("feedback").setOutputMarkupPlaceholderTag(true));
        }


        @Override
        public void onEvent(IEvent<?> event) {
            super.onEvent(event);
            if (event.getPayload() instanceof ErrorEvent) {
                onError((ErrorEvent) event.getPayload());
            }
        }


        /**
         * Form validation failed.
         * 
         * @param event
         *            the event payload
         */
        private void onError(ErrorEvent event) {
            controlGroup.add(AttributeAppender.append("class", " error"));
            event.getTarget().add(controlGroup);
        }
    }

}
