package pl.psnc.dl.wf4ever.portal.events.annotations;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to create a custom annotation.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotateEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public AnnotateEvent(AjaxRequestTarget target) {
        super(target);
    }

}
