package pl.psnc.dl.wf4ever.portal.behaviors;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.events.RoEvolutionLoadedEvent;

import com.google.common.eventbus.EventBus;

/**
 * The behavior that loads RO evolution metadata.
 * 
 * @author piotrekhol
 * 
 */
public final class EvolutionInfoLoadBehavior extends OnDomReadyAjaxBehaviour {

    /** id. */
    private static final long serialVersionUID = -3101949124011172256L;

    /** Logger. */
    static final Logger LOG = Logger.getLogger(RoLoadBehavior.class);

    /** Research object model. */
    private IModel<ResearchObject> model;

    /** Event bus model to post an event when finished. */
    private IModel<EventBus> eventBusModel;

    /** Feedback panel to update if there is an error. */
    private MyFeedbackPanel feedbackPanel;


    /**
     * Constructor.
     * 
     * @param feedbackPanel2
     *            Feedback panel to update if there is an error
     * @param model
     *            Research object model
     * @param eventBusModel
     *            Event bus model to post an event when finished
     */
    public EvolutionInfoLoadBehavior(MyFeedbackPanel feedbackPanel2, IModel<ResearchObject> model,
            IModel<EventBus> eventBusModel) {
        this.feedbackPanel = feedbackPanel2;
        this.model = model;
        this.eventBusModel = eventBusModel;
    }


    @Override
    protected void respond(AjaxRequestTarget target) {
        try {
            if (!model.getObject().isEvolutionInformationLoaded()) {
                model.getObject().loadEvolutionInformation();
                eventBusModel.getObject().post(new RoEvolutionLoadedEvent(target));
            }
        } catch (Exception e) {
            Session.get().error("Could not load the evolution information: " + e.getLocalizedMessage());
            LOG.error("Could not load the evolution information", e);
            target.add(feedbackPanel);
        }
        super.respond(target);
    }
}
