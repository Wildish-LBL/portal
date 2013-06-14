package pl.psnc.dl.wf4ever.portal.events.annotations;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

/**
 * An annotation has been deleted.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationDeletedEvent extends AbstractAnnotationEditedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param annotableModel
     *            the resource that should be annotated
     */
    public AnnotationDeletedEvent(AjaxRequestTarget target, IModel<? extends Annotable> annotableModel) {
        super(target, annotableModel);
    }

}
