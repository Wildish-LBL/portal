package pl.psnc.dl.wf4ever.portal.pages.ro.behaviors;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.checklist.client.ChecklistEvaluationService;
import org.purl.wf4ever.checklist.client.EvaluationResult;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.events.QualityEvaluatedEvent;

import com.google.common.eventbus.EventBus;

/**
 * The behavior that calls the checklist evaluation service.
 * 
 * @author piotrekhol
 * 
 */
public final class ChecklistEvaluateBehavior extends OnDomReadyAjaxBehaviour {

    /** Logger. */
    static final Logger LOG = Logger.getLogger(ChecklistEvaluateBehavior.class);

    /** id. */
    private static final long serialVersionUID = -3466472792372594013L;

    /** Research object model. */
    private IModel<ResearchObject> model;

    /** Event bus model to post an event when finished. */
    private IModel<EventBus> eventBusModel;

    /** Feedback panel to update if there is an error. */
    private MyFeedbackPanel feedbackPanel;

    /** Checklist evaluation service. */
    private ChecklistEvaluationService service;

    /** Model to store the result. */
    private IModel<EvaluationResult> resultModel;


    /**
     * Constructor.
     * 
     * @param feedbackPanel2
     *            Feedback panel to update if there is an error
     * @param model
     *            Research object model
     * @param eventBusModel
     *            Event bus model to post an event when finished
     * @param service
     *            Checklist evaluation service
     * @param resultModel
     *            Model to store the result
     */
    public ChecklistEvaluateBehavior(MyFeedbackPanel feedbackPanel2, IModel<ResearchObject> model,
            IModel<EventBus> eventBusModel, ChecklistEvaluationService service, IModel<EvaluationResult> resultModel) {
        this.feedbackPanel = feedbackPanel2;
        this.model = model;
        this.eventBusModel = eventBusModel;
        this.service = service;
        this.resultModel = resultModel;
    }


    @Override
    protected void respond(AjaxRequestTarget target) {
        try {
            resultModel.setObject(service.evaluate(model.getObject().getUri(), "ready-to-release"));
            eventBusModel.getObject().post(new QualityEvaluatedEvent(target));
        } catch (Exception e) {
            Session.get().error("Could not calculate the quality: " + e.getLocalizedMessage());
            target.add(feedbackPanel);
            LOG.error("Could not calculate the quality", e);
        }
        super.respond(target);
    }
}
