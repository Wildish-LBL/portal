package pl.psnc.dl.wf4ever.portal.events.annotations;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

/**
 * User clicked a button to show the input for adding a comment.
 * 
 * @author piotrekhol
 * 
 */
public class CommentAddClickedEvent extends AnnotationAddClickedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param annotableModel
     *            the resource that should be commented
     */
    public CommentAddClickedEvent(AjaxRequestTarget target, IModel<? extends Annotable> annotableModel) {
        super(target, annotableModel);
    }

}
