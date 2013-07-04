package pl.psnc.dl.wf4ever.portal.behaviors;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.events.RoLoadedEvent;

import com.google.common.eventbus.EventBus;

/**
 * The behavior that loads the RO metadata.
 * 
 * @author piotrekhol
 * 
 */
public class RoLoadBehavior extends OnDomReadyAjaxBehaviour {

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
    public RoLoadBehavior(MyFeedbackPanel feedbackPanel2, IModel<ResearchObject> model, IModel<EventBus> eventBusModel) {
        this.feedbackPanel = feedbackPanel2;
        this.model = model;
        this.eventBusModel = eventBusModel;
    }


    /** id. */
    private static final long serialVersionUID = -7378820165444166190L;


    @Override
    protected void respond(AjaxRequestTarget target) {
        if (!model.getObject().isLoaded()) {
            try {
                model.getObject().load();
                eventBusModel.getObject().post(new RoLoadedEvent(target));
            } catch (Exception e) {
                Session.get().error("Research object cannot be loaded: " + e.getMessage());
                LOG.error("Research object cannot be loaded", e);
                target.add(feedbackPanel);
            }
        }
        super.respond(target);
    }
}
