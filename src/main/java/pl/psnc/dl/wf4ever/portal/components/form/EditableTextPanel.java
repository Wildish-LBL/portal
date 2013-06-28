package pl.psnc.dl.wf4ever.portal.components.form;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationCancelledEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationDeletedEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.ApplyEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.CancelEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.DeleteEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.EditEvent;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * An editable text field.
 * 
 * @author pejot
 * 
 */
public class EditableTextPanel extends EventPanel {

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EditableTextPanel.class);

    /** id. */
    private static final long serialVersionUID = 1L;

    /** should the delete button be visible. */
    private boolean canDelete = true;

    /** The read only view of the field. */
    private Fragment viewFragment;

    /** The editable view of the field. */
    private Fragment editFragment;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the model of the value of the field
     * @param eventBusModel
     *            event bus model for triple add/edit/delete events
     * @param multipleLines
     *            should the edit field be a text area
     * @param editMode
     *            should the field start in an edit mode
     */
    public EditableTextPanel(String id, AnnotationTripleModel model, final IModel<EventBus> eventBusModel,
            boolean multipleLines, boolean editMode) {
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
        viewFragment = newViewFragment(model, internalEventBusModel);
        if (multipleLines) {
            editFragment = new EditFragment("content", "editMultiple", this, model, internalEventBusModel,
                    multipleLines);
        } else {
            editFragment = new EditFragment("content", "editSingle", this, model, internalEventBusModel, multipleLines);
        }
        add(editMode ? editFragment : viewFragment);
    }


    /**
     * Create new fragment for the read-only view.
     * 
     * @param model
     *            value model
     * @param internalEventBusModel
     *            event bus for button clicks
     * @return a fragment
     */
    protected Fragment newViewFragment(AnnotationTripleModel model, IModel<EventBus> internalEventBusModel) {
        return new ViewFragment("content", "view", this, model, internalEventBusModel);
    }


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            model for the value displayed
     * @param eventBusModel
     *            event bus model for the value changes
     * @param multipleLines
     *            should the edit field be a text area
     */
    public EditableTextPanel(String id, AnnotationTripleModel model, final IModel<EventBus> eventBusModel,
            boolean multipleLines) {
        this(id, model, eventBusModel, multipleLines, false);
    }


    public boolean isCanDelete() {
        return canDelete;
    }


    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
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

        /** Delete. */
        private AjaxButton deleteButton;

        /** Label with value. */
        private NotSetLabel notSetLabel;

        /** Form for button and label. */
        protected Form<Void> form;


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
        public ViewFragment(String id, String markupId, MarkupContainer markupProvider, AnnotationTripleModel model,
                final IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);
            form = new Form<Void>("form");
            add(form);
            notSetLabel = new NotSetLabel("text", model.getValueModel());
            form.add(notSetLabel);
            form.add(new AuthenticatedAjaxEventButton("edit", form, internalEventBusModel, EditEvent.class));
            deleteButton = new AuthenticatedAjaxEventButton("delete", form, internalEventBusModel, DeleteEvent.class);
            form.add(deleteButton);
        }


        @SuppressWarnings("unchecked")
        @Override
        public MarkupContainer setDefaultModel(IModel<?> model) {
            notSetLabel.setOriginalModel((IModel<String>) model);
            return super.setDefaultModel(model);
        }


        @Override
        protected void onConfigure() {
            super.onConfigure();
            deleteButton.setVisible(canDelete);
        }

    }


    /**
     * A label that displays "Not set" when the value is null.
     * 
     * @author piotrekhol
     * 
     */
    class NotSetLabel extends Label {

        /** id. */
        private static final long serialVersionUID = 5810881670929377406L;

        /** The model for the value, can have a null value. */
        private IModel<String> originalModel;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param model
         *            the model for the value, can have a null value
         */
        public NotSetLabel(String id, IModel<String> model) {
            super(id);
            setDefaultModel(new PropertyModel<String>(this, "valueOrNotSet"));
            originalModel = model;
        }


        public IModel<String> getOriginalModel() {
            return originalModel;
        }


        public void setOriginalModel(IModel<String> originalModel) {
            this.originalModel = originalModel;
        }


        @Override
        protected void onConfigure() {
            super.onConfigure();
            setEscapeModelStrings(originalModel.getObject() != null);
        }


        /**
         * If the original value is not null, return it, otherwise return "<em>Not set</em>".
         * 
         * @return the original value or a replacement text
         */
        public String getValueOrNotSet() {
            if (originalModel.getObject() != null) {
                return originalModel.getObject();
            } else {
                return "<em>Not set</em>";
            }
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

        /** the input area (can be a text field or a text area). */
        private AbstractTextComponent<String> textArea;


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
         * @param multipleLines
         *            should the edit area have multiple lines
         */
        public EditFragment(String id, String markupId, MarkupContainer markupProvider, AnnotationTripleModel model,
                final IModel<EventBus> internalEventBusModel, boolean multipleLines) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);
            Form<?> form = new Form<Void>("form");
            add(form);
            textArea = multipleLines ? new TextArea<>("text", model.getValueModel()) : new TextField<>("text",
                    model.getValueModel());
            form.add(textArea);
            form.add(new AuthenticatedAjaxEventButton("apply", form, internalEventBusModel, ApplyEvent.class));
            form.add(new AuthenticatedAjaxEventButton("cancel", form, internalEventBusModel, CancelEvent.class)
                    .setDefaultFormProcessing(false));
        }


        @Override
        public MarkupContainer setDefaultModel(IModel<?> model) {
            textArea.setDefaultModel(model);
            return super.setDefaultModel(model);
        }

    }

}
