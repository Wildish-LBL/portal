package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A resource has been deleted.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceDeletedEvent extends AggregationChangedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ResourceDeletedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
