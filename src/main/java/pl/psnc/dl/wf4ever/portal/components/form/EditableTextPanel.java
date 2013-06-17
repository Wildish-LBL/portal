package pl.psnc.dl.wf4ever.portal.components.form;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationCancelledEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationDeletedEvent;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * An editable text field.
 * 
 * @author pejot
 * 
 */
public class EditableTextPanel extends Panel {

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EditableTextPanel.class);

    /** id. */
    private static final long serialVersionUID = 1L;

    /** should the delete button be visible. */
    private boolean canDelete = true;

    /** event bus model for triple add/edit/delete events. */
    private IModel<EventBus> eventBusModel;

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
        super(id, model);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        this.eventBusModel = eventBusModel;
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
    protected Fragment newViewFragment(IModel<String> model, IModel<EventBus> internalEventBusModel) {
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
    @SuppressWarnings("unchecked")
    @Subscribe
    public void onDelete(DeleteEvent event) {
        ((IModel<String>) this.getDefaultModel()).setObject(null);
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
        public ViewFragment(String id, String markupId, MarkupContainer markupProvider, IModel<String> model,
                final IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);
            form = new Form<Void>("form");
            add(form);
            notSetLabel = new NotSetLabel("text", model);
            form.add(notSetLabel);
            form.add(new EditButton("edit", internalEventBusModel));
            deleteButton = new DeleteButton("delete", internalEventBusModel);
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


        /**
         * AJAX button. Needs a separate class because it calls an event that is a nested class.
         * 
         * @author piotrekhol
         * 
         */
        //FIXME we might want to use a standard AuthenticatedAjaxButton once it can call nested events.
        @AuthorizeAction(action = Action.ENABLE, roles = { Roles.USER })
        private final class EditButton extends AjaxEventButton {

            /** id. */
            private static final long serialVersionUID = 6073205674452657839L;


            /**
             * Constructor.
             * 
             * @param id
             *            wicket id
             * @param internalEventBusModel
             *            event bus model for clicks
             */
            private EditButton(String id, IModel<EventBus> internalEventBusModel) {
                super(id, internalEventBusModel, EditEvent.class);
            }


            @Override
            protected AbstractAjaxEvent newEvent(AjaxRequestTarget target) {
                return new EditEvent(target);
            }

        }


        /**
         * AJAX button. Needs a separate class because it calls an event that is a nested class.
         * 
         * @author piotrekhol
         * 
         */
        //FIXME we might want to use a standard AuthenticatedAjaxButton once it can call nested events.
        @AuthorizeAction(action = Action.ENABLE, roles = { Roles.USER })
        private final class DeleteButton extends AjaxEventButton {

            /** id. */
            private static final long serialVersionUID = 6073205674452657839L;


            /**
             * Constructor.
             * 
             * @param id
             *            wicket id
             * @param internalEventBusModel
             *            event bus model for clicks
             */
            private DeleteButton(String id, IModel<EventBus> internalEventBusModel) {
                super(id, internalEventBusModel, DeleteEvent.class);
            }


            @Override
            protected AbstractAjaxEvent newEvent(AjaxRequestTarget target) {
                return new DeleteEvent(target);
            }
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
        public EditFragment(String id, String markupId, MarkupContainer markupProvider, IModel<String> model,
                final IModel<EventBus> internalEventBusModel, boolean multipleLines) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);
            Form<?> form = new Form<Void>("form");
            add(form);
            textArea = multipleLines ? new TextArea<>("text", model) : new TextField<>("text", model);
            form.add(textArea);
            form.add(new ApplyButton("apply", internalEventBusModel));
            form.add(new CancelButton("cancel", internalEventBusModel));
        }


        @Override
        public MarkupContainer setDefaultModel(IModel<?> model) {
            textArea.setDefaultModel(model);
            return super.setDefaultModel(model);
        }


        /**
         * AJAX button. Needs a separate class because it calls an event that is a nested class.
         * 
         * @author piotrekhol
         * 
         */
        //FIXME we might want to use a standard AuthenticatedAjaxButton once it can call nested events.
        @AuthorizeAction(action = Action.ENABLE, roles = { Roles.USER })
        private final class ApplyButton extends AjaxEventButton {

            /** id. */
            private static final long serialVersionUID = 6073205674452657839L;


            /**
             * Constructor.
             * 
             * @param id
             *            wicket id
             * @param internalEventBusModel
             *            event bus model for clicks
             */
            private ApplyButton(String id, IModel<EventBus> internalEventBusModel) {
                super(id, internalEventBusModel, ApplyEvent.class);
            }


            @Override
            protected AbstractAjaxEvent newEvent(AjaxRequestTarget target) {
                return new ApplyEvent(target);
            }
        }


        /**
         * AJAX button. Needs a separate class because it calls an event that is a nested class.
         * 
         * @author piotrekhol
         * 
         */
        //FIXME we might want to use a standard AuthenticatedAjaxButton once it can call nested events.
        @AuthorizeAction(action = Action.ENABLE, roles = { Roles.USER })
        private final class CancelButton extends AjaxEventButton {

            /** id. */
            private static final long serialVersionUID = 6073205674452657839L;


            /**
             * Constructor.
             * 
             * @param id
             *            wicket id
             * @param internalEventBusModel
             *            event bus model for clicks
             */
            private CancelButton(String id, IModel<EventBus> internalEventBusModel) {
                super(id, internalEventBusModel, CancelEvent.class);
                setDefaultFormProcessing(false);
            }


            @Override
            protected AbstractAjaxEvent newEvent(AjaxRequestTarget target) {
                return new CancelEvent(target);
            }

        }

    }


    /** A click event. */
    class EditEvent extends AbstractAjaxEvent {

        /**
         * Constructor.
         * 
         * @param target
         *            AJAX target
         */
        public EditEvent(AjaxRequestTarget target) {
            super(target);
        }

    }


    /** A click event. */
    class DeleteEvent extends AbstractAjaxEvent {

        /**
         * Constructor.
         * 
         * @param target
         *            AJAX target
         */
        public DeleteEvent(AjaxRequestTarget target) {
            super(target);
        }

    }


    /** A click event. */
    class ApplyEvent extends AbstractAjaxEvent {

        /**
         * Constructor.
         * 
         * @param target
         *            AJAX target
         */
        public ApplyEvent(AjaxRequestTarget target) {
            super(target);
        }

    }


    /** A click event. */
    class CancelEvent extends AbstractAjaxEvent {

        /**
         * Constructor.
         * 
         * @param target
         *            AJAX target
         */
        public CancelEvent(AjaxRequestTarget target) {
            super(target);
        }

    }

}