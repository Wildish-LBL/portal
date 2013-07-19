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

    /** RO title. */
    private final String title;

    /** RO description. */
    private final String description;


    /**
     * Constructor.
     * 
     * @param target
     *            response target
     * @param roId
     *            RO id
     * @param template
     *            RO template (may be null)
     * @param title
     *            RO title
     * @param description
     *            RO description
     */
    public RoCreateReadyEvent(AjaxRequestTarget target, String roId, ResearchObjectTemplate template, String title,
            String description) {
        super(target);
        this.roId = roId;
        this.template = template;
        this.title = title;
        this.description = description;
    }


    public String getRoId() {
        return roId;
    }


    public ResearchObjectTemplate getTemplate() {
        return template;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }

}
