package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to move a resource between folders.
 * 
 * @author piotrekhol
 * 
 */
public class MoveEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public MoveEvent(AjaxRequestTarget target) {
        super(target);
    }

}
