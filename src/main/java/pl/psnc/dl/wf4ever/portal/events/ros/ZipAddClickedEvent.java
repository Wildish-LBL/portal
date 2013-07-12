package pl.psnc.dl.wf4ever.portal.events.ros;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to create a new RO from a ZIP archive.
 * 
 * @author piotrekhol
 * 
 */
public class ZipAddClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ZipAddClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
