package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User has changed the minim model that should be used.
 * 
 * @author piotrekhol
 * 
 */
public class MinimModelChangedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public MinimModelChangedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
