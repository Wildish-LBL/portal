package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User wants to add a link to a resource in a folder.
 * 
 * @author piotrekhol
 * 
 */
public class AddLinkEvent extends AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public AddLinkEvent(AjaxRequestTarget target) {
        super(target);
    }

}
