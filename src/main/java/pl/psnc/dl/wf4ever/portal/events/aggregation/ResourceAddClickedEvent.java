package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to add a resource.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceAddClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ResourceAddClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
