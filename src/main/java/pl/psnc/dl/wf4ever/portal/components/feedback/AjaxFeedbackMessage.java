package pl.psnc.dl.wf4ever.portal.components.feedback;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;

/**
 * Feedback message that is sent using AJAX.
 * 
 * @author piotrekhol
 * 
 */
public class AjaxFeedbackMessage extends FeedbackMessage {

    /** id. */
    private static final long serialVersionUID = -6816787727697200076L;

    /** AJAX target. */
    private final AjaxRequestTarget target;


    /**
     * Construct using fields.
     * 
     * @param reporter
     *            The message reporter
     * @param message
     *            The actual message. Must not be <code>null</code>.
     * @param level
     *            The level of the message
     * @param target
     *            AJAX target
     */
    public AjaxFeedbackMessage(Component reporter, Serializable message, int level, AjaxRequestTarget target) {
        super(reporter, message, level);
        this.target = target;
    }


    public AjaxRequestTarget getTarget() {
        return target;
    }

}
