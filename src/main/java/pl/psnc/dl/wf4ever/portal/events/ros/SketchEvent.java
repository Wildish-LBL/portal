package pl.psnc.dl.wf4ever.portal.events.ros;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

public class SketchEvent extends AbstractAjaxEvent {
	/**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public SketchEvent(AjaxRequestTarget target) {
        super(target);
    }

}
