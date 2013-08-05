package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.aggregation.AggregationChangedEvent;

/**
 * A workflow has been transformed.
 * 
 * @author piotrekhol
 * 
 */
public class WorkflowTransformedEvent extends AggregationChangedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public WorkflowTransformedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
