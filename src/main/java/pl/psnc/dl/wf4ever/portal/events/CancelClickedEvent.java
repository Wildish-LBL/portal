package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User clicked Cancel.
 * 
 * @author piotrekhol
 * 
 */
public class CancelClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public CancelClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
