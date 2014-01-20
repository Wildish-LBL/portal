package pl.psnc.dl.wf4ever.portal.events.permissions;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;


public class PermissionDeletedClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

	public PermissionDeletedClickedEvent(AjaxRequestTarget target) {
		super(target);
	}

}
