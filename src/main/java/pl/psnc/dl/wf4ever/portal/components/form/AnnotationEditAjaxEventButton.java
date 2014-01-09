package pl.psnc.dl.wf4ever.portal.components.form;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * An AJAX button that is related to an annotable object. The button is disabled when the user has not signed in.
 * 
 * @author piotrekhol
 * 
 */
@AuthorizeAction(action = Action.ENABLE, roles = { "editor" })
public class AnnotationEditAjaxEventButton extends AjaxEventButton {

    /** id. */
    private static final long serialVersionUID = 4715910030458646408L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AnnotationEditAjaxEventButton.class);

    /** the resource that should be commented. */
    private IModel<? extends Annotable> annotableModel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     * @param form
     *            for which will be validated
     * @param annotableModel
     *            the resource that should be commented
     * @param component
     *            the root of the DOM subtree that will be notified
     * @param eventClass
     *            the class of the event to post. It must have a constructor (target, annotableModel).
     */
    public AnnotationEditAjaxEventButton(String id, Form<?> form, IModel<? extends Annotable> annotableModel,
            IEventSink component, Class<? extends AbstractClickAjaxEvent> eventClass) {
        super(id, form, component, eventClass);
        this.annotableModel = annotableModel;
    }


    @Override
    protected AbstractAjaxEvent newEvent(AjaxRequestTarget target) {
        try {
            return (AbstractAjaxEvent) eventClass.getConstructor(AjaxRequestTarget.class, IModel.class).newInstance(
                target, annotableModel);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            LOG.error("Can't create the (target,model) event", e);
            return null;
        }
    }

}
