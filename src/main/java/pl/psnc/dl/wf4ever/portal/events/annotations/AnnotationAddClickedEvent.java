package pl.psnc.dl.wf4ever.portal.events.annotations;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User clicked a button to show the input for adding an annotation.
 * 
 * @author piotrekhol
 * 
 */
public abstract class AnnotationAddClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

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
    public AnnotationAddClickedEvent(AjaxRequestTarget target, IModel<? extends Annotable> annotableModel) {
        super(target);
        this.annotableModel = annotableModel;
    }


    public IModel<? extends Annotable> getAnnotableModel() {
        return annotableModel;
    }

}
