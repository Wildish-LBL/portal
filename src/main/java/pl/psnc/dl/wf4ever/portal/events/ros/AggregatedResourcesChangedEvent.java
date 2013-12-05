package pl.psnc.dl.wf4ever.portal.events.ros;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

public class AggregatedResourcesChangedEvent extends AbstractAjaxEvent {
	/**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public AggregatedResourcesChangedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
