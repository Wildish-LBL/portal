package pl.psnc.dl.wf4ever.portal.modals;

import java.util.List;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.CancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.OkClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoDeleteClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoDeleteReadyEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
public class DeleteROModal extends EventPanel {

    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param eventBusModel
     *            event bus
     * @param toDelete
     *            ROs to delete
     */
    public DeleteROModal(String id, final IModel<EventBus> eventBusModel, final IModel<List<ResearchObject>> toDelete) {
        super(id, toDelete, eventBusModel);
        setOutputMarkupId(true);
        final Form<?> form = new Form<Void>("form");
        add(form);

        LoadableDetachableModel<EventBus> internalEventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        internalEventBusModel.getObject().register(this);

        form.add(new Label("deleteCnt", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (toDelete.getObject().size() == 1) {
                    return "1 Research Object";
                }
                return toDelete.getObject().size() + " Research Objects";
            }
        }));

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
                "DeleteROModal.js")));
    }


    /**
     * Show itself.
     * 
     * @param event
     *            AJAX event
     */
    @SuppressWarnings("unchecked")
    @Subscribe
    public void onRoDelete(RoDeleteClickedEvent event) {
        if (!((List<ResearchObject>) getDefaultModelObject()).isEmpty()) {
            event.getTarget().add(this);
            event.getTarget().appendJavaScript("$('#delete-ro-modal').modal('show')");
        }
    }


    /**
     * Post an event and hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onOk(OkClickedEvent event) {
        eventBusModel.getObject().post(new RoDeleteReadyEvent(event.getTarget()));
        event.getTarget().prependJavaScript("$('#delete-ro-modal').modal('hide')");
    }


    /**
     * Hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onCancel(CancelClickedEvent event) {
        event.getTarget().appendJavaScript("$('#delete-ro-modal').modal('hide')");
    }
}
