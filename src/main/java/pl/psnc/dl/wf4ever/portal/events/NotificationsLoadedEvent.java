package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * The notifications of the RO have been loaded.
 * 
 * @author piotrekhol
 * 
 */
public class NotificationsLoadedEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public NotificationsLoadedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
