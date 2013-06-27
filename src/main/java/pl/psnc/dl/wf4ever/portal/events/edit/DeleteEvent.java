package pl.psnc.dl.wf4ever.portal.events.edit;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/** A click event. */
public class DeleteEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX target
     */
    public DeleteEvent(AjaxRequestTarget target) {
        super(target);
    }

}
