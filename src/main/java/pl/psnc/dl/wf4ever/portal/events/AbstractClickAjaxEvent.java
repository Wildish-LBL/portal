package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * An AJAX abstract event for user clicks.
 * 
 * @author piotrekhol
 * 
 */
public abstract class AbstractClickAjaxEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public AbstractClickAjaxEvent(AjaxRequestTarget target) {
        super(target);
    }
}
