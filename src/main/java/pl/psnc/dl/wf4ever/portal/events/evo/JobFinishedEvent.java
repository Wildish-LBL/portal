package pl.psnc.dl.wf4ever.portal.events.evo;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * A snapshot or release has been created.
 * 
 * @author piotrekhol
 * 
 */
public class JobFinishedEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public JobFinishedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
