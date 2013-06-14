package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * An abstract event that knows an AJAX request target.
 * 
 * @author piotrekhol
 * 
 */
public abstract class AbstractAjaxEvent {

    /** The AJAX request target. */
    private final AjaxRequestTarget target;


    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public AbstractAjaxEvent(AjaxRequestTarget target) {
        this.target = target;
    }


    public AjaxRequestTarget getTarget() {
        return target;
    }

}
