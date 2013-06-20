package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User clicked OK.
 * 
 * @author piotrekhol
 * 
 */
public class OkClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public OkClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
