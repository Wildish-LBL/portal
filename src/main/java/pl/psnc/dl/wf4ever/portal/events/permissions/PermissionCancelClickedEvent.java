package pl.psnc.dl.wf4ever.portal.events.permissions;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;


public class PermissionCancelClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

	public PermissionCancelClickedEvent(AjaxRequestTarget target) {
		super(target);
	}

}
