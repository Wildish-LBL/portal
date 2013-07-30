package pl.psnc.dl.wf4ever.portal.components.form;

import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.markup.html.form.Form;

/**
 * A button the creates shows a "busy" indicator when clicked.
 * 
 * @author piotrekhol
 * 
 */
@AuthorizeAction(action = Action.ENABLE, roles = { Roles.USER })
public abstract class AuthenticatedAjaxDecoratedButton extends AjaxDecoratedButton {

    /** id. */
    private static final long serialVersionUID = -2527416440222820413L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     * @param form
     *            for which will be validated
     */
    public AuthenticatedAjaxDecoratedButton(String id, Form<?> form) {
        super(id, form);
    }


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     */
    public AuthenticatedAjaxDecoratedButton(String id) {
        super(id);
    }

}
