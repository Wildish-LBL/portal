package pl.psnc.dl.wf4ever.portal.events.evo;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * A release has been created.
 * 
 * @author piotrekhol
 * 
 */
public class ReleaseCreatedEvent extends JobFinishedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ReleaseCreatedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
