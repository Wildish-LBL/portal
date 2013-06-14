package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * The RO metadata have been loaded.
 * 
 * @author piotrekhol
 * 
 */
public class RoLoadedEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public RoLoadedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
