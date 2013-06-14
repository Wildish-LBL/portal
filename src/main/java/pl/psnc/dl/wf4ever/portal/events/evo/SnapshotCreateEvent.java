package pl.psnc.dl.wf4ever.portal.events.evo;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to create a snapshot.
 * 
 * @author piotrekhol
 * 
 */
public class SnapshotCreateEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public SnapshotCreateEvent(AjaxRequestTarget target) {
        super(target);
    }

}
