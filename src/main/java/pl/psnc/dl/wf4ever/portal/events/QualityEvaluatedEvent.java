package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * The quality of the RO has been evaluated.
 * 
 * @author piotrekhol
 * 
 */
public class QualityEvaluatedEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public QualityEvaluatedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
