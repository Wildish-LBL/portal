package pl.psnc.dl.wf4ever.portal.components.annotations;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationCancelledEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.ApplyEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.CancelEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.EditEvent;
import pl.psnc.dl.wf4ever.portal.model.NotSetModel;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;
import pl.psnc.dl.wf4ever.portal.model.ResourceTypeModel;
import pl.psnc.dl.wf4ever.portal.model.SanitizedModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A panel that allows to choose a resource type from a predefined list.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTypePanel extends EventPanel {

    /**
     * Read-only view fragment.
     * 
     * @author piotrekhol
     * 
     */
    protected class ViewFragment extends Fragment {

        /** id. */
        private static final long serialVersionUID = -5449184839717649528L;


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
        public ViewFragment(String id, String markupId, MarkupContainer markupProvider, ResourceTypeModel model,
                IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider, model);
            Form<Void> form = new Form<Void>("form");
            add(form);
            form.add(new Label("text", new SanitizedModel(new NotSetModel(new PropertyModel<String>(model, "name"))))
                    .setEscapeModelStrings(false));
            form.add(new AuthenticatedAjaxEventButton("edit", form, internalEventBusModel, EditEvent.class));
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
         * the resource type that is currently selected. This is not automatically sent to the model, only after the
         * user clicks OK.
         */
        private ResourceType resourceType;


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
        public EditFragment(String id, String markupId, MarkupContainer markupProvider, ResourceTypeModel model,
                final IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider, model);
            resourceType = model.getObject();
            setOutputMarkupPlaceholderTag(true);
            Form<?> form = new Form<Void>("form");
            add(form);
            form.add(new ResourceTypeDropDownChoice("typeList", new PropertyModel<ResourceType>(this, "resourceType")));
            form.add(new AuthenticatedAjaxEventButton("apply", form, internalEventBusModel, ApplyEvent.class));
            form.add(new AuthenticatedAjaxEventButton("cancel", form, internalEventBusModel, CancelEvent.class)
                    .setDefaultFormProcessing(false));
        }


        public ResourceType getResourceType() {
            return resourceType;
        }


        public void setResourceType(ResourceType resourceType) {
            this.resourceType = resourceType;
        }

    }


    /** id. */
    private static final long serialVersionUID = -2277604858752974738L;

    /** The read-only view. */
    private ViewFragment viewFragment;

    /** The edit view. */
    private EditFragment editFragment;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the model of the value of the field
     * @param eventBusModel
     *            event bus model for triple add/edit/delete events
     */
    public ResourceTypePanel(String id, ResourceTypeModel model, IModel<EventBus> eventBusModel) {
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
        viewFragment = new ViewFragment("content", "view", this, model, internalEventBusModel);
        editFragment = new EditFragment("content", "edit", this, model, internalEventBusModel);
        add(viewFragment);
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
        //set the value only when the user clicked OK
        ((ResourceTypeModel) this.getDefaultModel()).setObject(editFragment.getResourceType());
        if (eventBusModel != null && eventBusModel.getObject() != null) {
            IModel<? extends Annotable> annotable = ((ResourceTypeModel) this.getDefaultModel()).getResourceModel();
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
            IModel<? extends Annotable> annotable = ((ResourceTypeModel) this.getDefaultModel()).getResourceModel();
            eventBusModel.getObject().post(new AnnotationCancelledEvent(event.getTarget(), annotable));
        }
    }

}
