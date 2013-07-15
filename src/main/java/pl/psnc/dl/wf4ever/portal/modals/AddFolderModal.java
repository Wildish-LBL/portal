package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.events.CancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.OkClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddReadyEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
public class AddFolderModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = -8887521104588150784L;
    /** Folder name. */
    private String name;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param eventBusModel
     *            event bus
     */
    public AddFolderModal(String id, final IModel<EventBus> eventBusModel) {
        super(id, eventBusModel, "add-folder-modal", "Add folder");
        modal.add(new RequiredTextField<>("folder-name", new PropertyModel<String>(this, "name")));
    }


    /**
     * Show itself.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAddFolderClicked(FolderAddClickedEvent event) {
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
        eventBusModel.getObject().post(new FolderAddReadyEvent(event.getTarget(), name));
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


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
