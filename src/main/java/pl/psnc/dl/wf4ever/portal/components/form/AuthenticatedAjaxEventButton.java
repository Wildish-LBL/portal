package pl.psnc.dl.wf4ever.portal.components.form;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

import com.google.common.eventbus.EventBus;

/**
 * An AJAX button that is disabled when the user has not signed in.
 * 
 * @author piotrekhol
 * 
 */
@AuthorizeAction(action = Action.ENABLE, roles = { Roles.USER })
public class AuthenticatedAjaxEventButton extends AjaxEventButton {

    /** id. */
    private static final long serialVersionUID = 4715910030458646408L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     * @param form
     *            for which will be validated
     * @param eventBusModel
     *            the event bus model to which the event is posted
     * @param eventClass
     *            the class of the event to post
     */
    public AuthenticatedAjaxEventButton(String id, Form<?> form, IModel<EventBus> eventBusModel,
            Class<? extends AbstractClickAjaxEvent> eventClass) {
        super(id, form, eventBusModel, eventClass);
    }

}
