package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * There is some AJAX error, for example the form has not validated.
 * 
 * @author piotrekhol
 * 
 */
public class ErrorEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ErrorEvent(AjaxRequestTarget target) {
        super(target);
    }

}
