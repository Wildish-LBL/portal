package pl.psnc.dl.wf4ever.portal.events.evo;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A snapshot has been created.
 * 
 * @author piotrekhol
 * 
 */
public class SnapshotCreatedEvent extends JobFinishedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public SnapshotCreatedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
