package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A resource has been moved to a folder.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceMovedEvent extends AggregationChangedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ResourceMovedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
