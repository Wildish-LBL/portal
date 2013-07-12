package pl.psnc.dl.wf4ever.portal.events.ros;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to delete an RO.
 * 
 * @author piotrekhol
 * 
 */
public class RoDeleteReadyEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            response target
     */
    public RoDeleteReadyEvent(AjaxRequestTarget target) {
        super(target);
    }
}
