package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A resource has been added.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceAddedEvent extends AggregationChangedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ResourceAddedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
