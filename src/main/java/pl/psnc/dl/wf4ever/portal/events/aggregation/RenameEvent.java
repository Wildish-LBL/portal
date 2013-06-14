package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * A resource has been renamed.
 * 
 * @author piotrekhol
 * 
 */
public class RenameEvent extends AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public RenameEvent(AjaxRequestTarget target) {
        super(target);
    }

}
