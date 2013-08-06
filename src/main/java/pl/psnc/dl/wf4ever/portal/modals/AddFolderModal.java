package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddReadyEvent;

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
     */
    public AddFolderModal(String id) {
        super(id, "add-folder-modal", "Add folder");
        modal.add(withFocus(new RequiredTextField<>("folder-name", new PropertyModel<String>(this, "name"))));
    }


    @Override
    public void onOk(AjaxRequestTarget target) {
        send(getPage(), Broadcast.BREADTH, new FolderAddReadyEvent(target, name));
        hide(target);
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
}
