package pl.psnc.dl.wf4ever.portal.events.annotations;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * An annotation has been changed (deleted, edited, created, etc).
 * 
 * @author piotrekhol
 * 
 */
public abstract class AbstractAnnotationEditedEvent extends AbstractAjaxEvent {

    /** The resource that should be commented. */
    private final IModel<? extends Annotable> annotableModel;


    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param annotableModel
     *            the resource that should be annotated
     */
    public AbstractAnnotationEditedEvent(AjaxRequestTarget target, IModel<? extends Annotable> annotableModel) {
        super(target);
        this.annotableModel = annotableModel;
    }


    public IModel<? extends Annotable> getAnnotableModel() {
        return annotableModel;
    }

}
