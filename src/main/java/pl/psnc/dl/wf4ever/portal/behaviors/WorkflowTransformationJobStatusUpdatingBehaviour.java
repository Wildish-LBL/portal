package pl.psnc.dl.wf4ever.portal.behaviors;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.util.time.Duration;
import org.purl.wf4ever.wf2ro.JobStatus;
import org.purl.wf4ever.wf2ro.JobStatus.State;

import pl.psnc.dl.wf4ever.portal.events.FeedbackEvent;
import pl.psnc.dl.wf4ever.portal.events.WorkflowTransformedEvent;

/**
 * A behavior for monitoring the progress of a workflow transformation.
 * 
 * @author piotrekhol
 * 
 */
public class WorkflowTransformationJobStatusUpdatingBehaviour extends AjaxSelfUpdatingTimerBehavior {

    /** id. */
    private static final long serialVersionUID = 6060461146505243329L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(WorkflowTransformationJobStatusUpdatingBehaviour.class);

    /** job status to check. */
    private final JobStatus status;


    /**
     * Constructor.
     * 
     * @param status
     *            job status to check
     */
    public WorkflowTransformationJobStatusUpdatingBehaviour(JobStatus status) {
        super(Duration.milliseconds(1000));
        this.status = status;
    }


    @Override
    protected void onPostProcessTarget(AjaxRequestTarget target) {
        super.onPostProcessTarget(target);
        Component component = getComponent();
        status.refresh();
        if (status.getState() != State.RUNNING) {
            stop(target);
            getComponent().remove(this);
        }
        switch (status.getState()) {
            case RUNNING:
                component.info(String.format("A workflow is being transformed (resources generated: %d)...", status
                        .getAdded().size()));
                break;
            case DONE:
                component.success(String.format("The workflow has been transformed."));
                component.send(component.getPage(), Broadcast.BREADTH, new WorkflowTransformedEvent(target));
                break;
            default:
                component.error(String.format("%s: %s", status.getState(), status.getReason()));
        }
        component.send(component.getPage(), Broadcast.BREADTH, new FeedbackEvent(target));
    }

}
