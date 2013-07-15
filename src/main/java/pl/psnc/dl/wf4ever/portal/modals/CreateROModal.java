package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

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
public class CreateROModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = 6366655308600651088L;
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
        super(id, eventBusModel, "create-ro-modal", "Create RO");
        form.add(new RequiredTextField<String>("roId", new PropertyModel<String>(this, "roId")));
    }


    /**
     * Show itself.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onRoCreate(RoCreateClickedEvent event) {
        show(event.getTarget());
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
        hide(event.getTarget());
    }


    /**
     * Hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onCancel(CancelClickedEvent event) {
        hide(event.getTarget());
    }


    public String getRoId() {
        return roId;
    }


    public void setRoId(String roId) {
        this.roId = roId;
    }
}
