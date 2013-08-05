package pl.psnc.dl.wf4ever.portal.behaviors;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;
import org.purl.wf4ever.rosrs.client.evo.JobStatus;
import org.purl.wf4ever.rosrs.client.evo.JobStatus.State;

import pl.psnc.dl.wf4ever.portal.events.evo.JobFinishedEvent;

import com.google.common.eventbus.EventBus;

/**
 * A behavior for monitoring the progress of a snapshot/release operation.
 * 
 * @author piotrekhol
 * 
 */
public class JobStatusUpdatingBehaviour extends AjaxSelfUpdatingTimerBehavior {

    /** id. */
    private static final long serialVersionUID = 6060461146505243329L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(JobStatusUpdatingBehaviour.class);

    /** job status to check. */
    private final JobStatus status;

    /** snapshot/release... */
    private String name;

    /** class of event posted when the job finished. */
    private Class<? extends JobFinishedEvent> eventClass;

    /** event bus model. */
    private IModel<EventBus> eventBusModel;


    /**
     * Constructor.
     * 
     * @param status
     *            job status to check
     * @param name
     *            "snapshot" or "release"
     * @param eventBusModel
     *            event bus model
     * @param eventClass
     *            class of event posted when the job finished
     */
    public JobStatusUpdatingBehaviour(JobStatus status, String name, IModel<EventBus> eventBusModel,
            Class<? extends JobFinishedEvent> eventClass) {
        super(Duration.milliseconds(1000));
        this.status = status;
        this.name = name;
        this.eventBusModel = eventBusModel;
        this.eventClass = eventClass;
        getComponent().info(String.format("A %s %s is being created...", name, status.getTarget()));
    }


    @Override
    protected void onPostProcessTarget(AjaxRequestTarget target) {
        super.onPostProcessTarget(target);
        status.refresh();
        if (status.getState() != State.RUNNING) {
            stop(target);
            getComponent().remove(this);
        }
        switch (status.getState()) {
            case RUNNING:
                getComponent().info(String.format("A %s %s is being created...", name, status.getTarget()));
                break;
            case DONE:
                getComponent().success(
                    String.format("%s %s has been created!", StringUtils.capitalize(name), status.getTarget()));
                JobFinishedEvent event = newEvent(target);
                eventBusModel.getObject().post(event);
                break;
            default:
                getComponent().error(String.format("%s: %s", status.getState(), status.getReason()));
        }
        target.add(getComponent());
    }


    /**
     * Create a new event.
     * 
     * @param target
     *            AJAX request target
     * @return an event or null if failed
     */
    protected JobFinishedEvent newEvent(AjaxRequestTarget target) {
        try {
            return eventClass.getConstructor(AjaxRequestTarget.class).newInstance(target);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            LOG.error("Can't create the default event", e);
            return null;
        }
    }
}
