package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.purl.wf4ever.rosrs.client.ResearchObject;

/**
 * The RO metadata have been loaded.
 * 
 * @author piotrekhol
 * 
 */
public class RoLoadedEvent extends AbstractAjaxEvent {

    private final ResearchObject researchObject;


    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param researchObject
     */
    public RoLoadedEvent(AjaxRequestTarget target, ResearchObject researchObject) {
        super(target);
        this.researchObject = researchObject;
    }


    public ResearchObject getResearchObject() {
        return researchObject;
    }
}
