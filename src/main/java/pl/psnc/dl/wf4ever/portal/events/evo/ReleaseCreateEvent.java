package pl.psnc.dl.wf4ever.portal.events.evo;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to create a release.
 * 
 * @author piotrekhol
 * 
 */
public class ReleaseCreateEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ReleaseCreateEvent(AjaxRequestTarget target) {
        super(target);
    }

}
