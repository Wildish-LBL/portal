package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User wants to see notifications of the RO.
 * 
 * @author piotrekhol
 * 
 */
public class NotificationsIndicatorClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public NotificationsIndicatorClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
