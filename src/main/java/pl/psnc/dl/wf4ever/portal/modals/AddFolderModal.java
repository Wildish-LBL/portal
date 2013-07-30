package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddReadyEvent;

import com.google.common.eventbus.EventBus;

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


    @Override
    public void onOk(AjaxRequestTarget target) {
        eventBusModel.getObject().post(new FolderAddReadyEvent(target, name));
        hide(target);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
