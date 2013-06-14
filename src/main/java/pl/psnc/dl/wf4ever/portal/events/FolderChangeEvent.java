package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User has changed the folder in a resource exploration panel.
 * 
 * @author piotrekhol
 * 
 */
public class FolderChangeEvent extends ResourceSelectedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public FolderChangeEvent(AjaxRequestTarget target) {
        super(target);
    }

}
