package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to upload a new version of the resource.
 * 
 * @author piotrekhol
 * 
 */
public class UpdateClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public UpdateClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
