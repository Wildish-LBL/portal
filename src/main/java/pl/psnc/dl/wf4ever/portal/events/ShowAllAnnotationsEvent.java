package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User wants to see all annotations of a resource.
 * 
 * @author piotrekhol
 * 
 */
public class ShowAllAnnotationsEvent extends AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ShowAllAnnotationsEvent(AjaxRequestTarget target) {
        super(target);
    }

}
