package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User has selected a resource in a resource exploration panel.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceSelectedEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ResourceSelectedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
