package pl.psnc.dl.wf4ever.portal.events.ros;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to create a new RO.
 * 
 * @author piotrekhol
 * 
 */
public class RoCreateReadyEvent extends AbstractAjaxEvent {

    /** Proposed RO id. */
    private final String roId;


    /**
     * Constructor.
     * 
     * @param target
     *            response target
     * @param roId
     *            RO id
     */
    public RoCreateReadyEvent(AjaxRequestTarget target, String roId) {
        super(target);
        this.roId = roId;
    }


    public String getRoId() {
        return roId;
    }

}
