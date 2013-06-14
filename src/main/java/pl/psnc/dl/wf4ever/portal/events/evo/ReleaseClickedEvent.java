package pl.psnc.dl.wf4ever.portal.events.evo;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to create a release.
 * 
 * @author piotrekhol
 * 
 */
public class ReleaseClickedEvent extends AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ReleaseClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
