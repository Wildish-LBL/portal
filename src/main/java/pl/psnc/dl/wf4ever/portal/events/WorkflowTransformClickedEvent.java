package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User clicked Transform.
 * 
 * @author piotrekhol
 * 
 */
public class WorkflowTransformClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public WorkflowTransformClickedEvent(AjaxRequestTarget target) {
        super(target);
    }
}
