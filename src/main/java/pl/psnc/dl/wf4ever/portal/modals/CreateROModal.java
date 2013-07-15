package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.CancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.OkClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoCreateClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoCreateReadyEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
public class CreateROModal extends EventPanel {

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** New RO id. */
    private String roId;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param eventBusModel
     *            event bus
     */
    public CreateROModal(String id, final IModel<EventBus> eventBusModel) {
        super(id, null, eventBusModel);
        final Form<?> form = new Form<Void>("form");
        form.add(new RequiredTextField<String>("roId", new PropertyModel<String>(this, "roId")));
        add(form);

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        LoadableDetachableModel<EventBus> internalEventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        internalEventBusModel.getObject().register(this);

        AjaxEventButton ok = new AjaxEventButton("ok", form, internalEventBusModel, OkClickedEvent.class);
        form.setDefaultButton(ok);
        form.add(ok);
        form.add(new AjaxEventButton("cancel", form, internalEventBusModel, CancelClickedEvent.class)
                .setDefaultFormProcessing(false));
        form.add(new AjaxEventButton("close", form, internalEventBusModel, CancelClickedEvent.class)
                .setDefaultFormProcessing(false));
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(getClass(),
                "CreateROModal.js")));
    }


    /**
     * Show itself.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onRoCreate(RoCreateClickedEvent event) {
        event.getTarget().appendJavaScript("$('#create-ro-modal').modal('show')");
    }


    /**
     * Post an event and hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onOk(OkClickedEvent event) {
        eventBusModel.getObject().post(new RoCreateReadyEvent(event.getTarget(), roId));
        event.getTarget().prependJavaScript("$('#create-ro-modal').modal('hide')");
        event.getTarget().add(feedbackPanel);
    }


    /**
     * Hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onCancel(CancelClickedEvent event) {
        event.getTarget().appendJavaScript("$('#create-ro-modal').modal('hide')");
        event.getTarget().add(feedbackPanel);
    }


    public String getRoId() {
        return roId;
    }


    public void setRoId(String roId) {
        this.roId = roId;
    }
}
