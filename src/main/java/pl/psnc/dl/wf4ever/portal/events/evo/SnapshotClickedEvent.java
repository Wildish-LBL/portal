package pl.psnc.dl.wf4ever.portal.events.evo;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to create a snapshot.
 * 
 * @author piotrekhol
 * 
 */
public class SnapshotClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public SnapshotClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
