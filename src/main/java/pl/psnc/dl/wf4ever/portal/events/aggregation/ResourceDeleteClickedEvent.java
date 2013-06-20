package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to delete a resource.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceDeleteClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ResourceDeleteClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
