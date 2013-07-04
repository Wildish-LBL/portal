package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.purl.wf4ever.rosrs.client.Folder;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to move a resource to a folder.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceMoveEvent extends AbstractAjaxEvent {

    /** The folder selected by the user. */
    private final Folder folder;


    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param folder
     *            the folder selected by the user
     */
    public ResourceMoveEvent(AjaxRequestTarget target, Folder folder) {
        super(target);
        this.folder = folder;
    }


    public Folder getFolder() {
        return folder;
    }

}
