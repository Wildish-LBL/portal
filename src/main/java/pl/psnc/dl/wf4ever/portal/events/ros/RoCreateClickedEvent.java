package pl.psnc.dl.wf4ever.portal.events.ros;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to create a new RO.
 * 
 * @author piotrekhol
 * 
 */
public class RoCreateClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public RoCreateClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
