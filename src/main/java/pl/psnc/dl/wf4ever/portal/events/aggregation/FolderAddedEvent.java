package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A folder has been added.
 * 
 * @author piotrekhol
 * 
 */
public class FolderAddedEvent extends AggregationChangedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public FolderAddedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
