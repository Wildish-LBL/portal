package pl.psnc.dl.wf4ever.portal.components.annotations;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationCancelledEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.ApplyEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.CancelEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.EditEvent;
import pl.psnc.dl.wf4ever.portal.model.wicket.MergedCollectionModel;
import pl.psnc.dl.wf4ever.portal.model.wicket.NotSetModel;
import pl.psnc.dl.wf4ever.portal.model.wicket.ResourceTypeModel;
import pl.psnc.dl.wf4ever.portal.model.wicket.SanitizedModel;

/**
 * A panel that allows to choose a resource type from a predefined list.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTypePanel extends Panel {

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
         */
        public ViewFragment(String id, String markupId, MarkupContainer markupProvider, ResourceTypeModel model) {
            super(id, markupId, markupProvider, model);
            Form<Void> form = new Form<Void>("form");
            add(form);
            form.add(new Label("text", new SanitizedModel(new NotSetModel(new MergedCollectionModel(model))))
                    .setEscapeModelStrings(false));
            form.add(new AuthenticatedAjaxEventButton("edit", form, ResourceTypePanel.this, EditEvent.class));
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
         * @param model
         *            value model
         */
        public EditFragment(String id, String markupId, MarkupContainer markupProvider, ResourceTypeModel model) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);
            Form<?> form = new Form<Void>("form");
            add(form);
            form.add(new ResourceTypeDropDownChoice("typeList", model));
            form.add(new AuthenticatedAjaxEventButton("apply", form, ResourceTypePanel.this, ApplyEvent.class));
            form.add(new AuthenticatedAjaxEventButton("cancel", form, ResourceTypePanel.this, CancelEvent.class)
                    .setDefaultFormProcessing(false));
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
     */
    public ResourceTypePanel(String id, ResourceTypeModel model) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);
        viewFragment = new ViewFragment("content", "view", this, model);
        editFragment = new EditFragment("content", "edit", this, model);
        add(viewFragment);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof EditEvent) {
            onEdit((EditEvent) event.getPayload());
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
     * Called when the apply button is clicked. Replace the edit fragment with view and post an event.
     * 
     * @param event
     *            click event
     */
    private void onApply(ApplyEvent event) {
        editFragment.replaceWith(viewFragment);
        event.getTarget().appendJavaScript("$('.tooltip').remove();");
        event.getTarget().add(this);
        IModel<? extends Annotable> annotable = ((ResourceTypeModel) this.getDefaultModel()).getResourceModel();
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
        IModel<? extends Annotable> annotable = ((ResourceTypeModel) this.getDefaultModel()).getResourceModel();
        send(getPage(), Broadcast.BREADTH, new AnnotationCancelledEvent(event.getTarget(), annotable));
    }

}
