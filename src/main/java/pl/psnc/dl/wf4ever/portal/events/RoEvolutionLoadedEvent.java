package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * The RO evolution metadata have been loaded.
 * 
 * @author piotrekhol
 * 
 */
public class RoEvolutionLoadedEvent extends AbstractAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public RoEvolutionLoadedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
