package pl.psnc.dl.wf4ever.portal.events.ros;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.model.template.ResearchObjectTemplate;

/**
 * User wants to create a new RO.
 * 
 * @author piotrekhol
 * 
 */
public class RoCreateReadyEvent extends AbstractAjaxEvent {

    /** Proposed RO id. */
    private final String roId;

    /** RO template. */
    private final ResearchObjectTemplate template;


    /**
     * Constructor.
     * 
     * @param target
     *            response target
     * @param roId
     *            RO id
     * @param template
     *            RO template (may be null)
     */
    public RoCreateReadyEvent(AjaxRequestTarget target, String roId, ResearchObjectTemplate template) {
        super(target);
        this.roId = roId;
        this.template = template;
    }


    public String getRoId() {
        return roId;
    }


    public ResearchObjectTemplate getTemplate() {
        return template;
    }

}
