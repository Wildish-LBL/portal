package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * The aggregation of the RO has changed (a resource has been added or deleted).
 * 
 * @author piotrekhol
 * 
 */
public class AggregationChangedEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public AggregationChangedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
