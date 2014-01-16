package pl.psnc.dl.wf4ever.portal.events.permissions;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;


public class PermissionApplyClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

	public PermissionApplyClickedEvent(AjaxRequestTarget target) {
		super(target);
	}

}
