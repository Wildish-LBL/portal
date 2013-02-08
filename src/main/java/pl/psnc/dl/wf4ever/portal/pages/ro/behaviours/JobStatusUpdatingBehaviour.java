package pl.psnc.dl.wf4ever.portal.pages.ro.behaviours;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.util.time.Duration;
import org.purl.wf4ever.rosrs.client.evo.JobStatus;
import org.purl.wf4ever.rosrs.client.evo.JobStatus.State;

public abstract class JobStatusUpdatingBehaviour extends AjaxSelfUpdatingTimerBehavior {

    /** id. */
    private final JobStatus status;
    private Component parent;
    /** snapshot/release... */
    private String name;
    /** id. */
    private static final long serialVersionUID = 6060461146505243329L;


    public JobStatusUpdatingBehaviour(Component parent, JobStatus status, String name) {
        super(Duration.milliseconds(1000));
        this.parent = parent;
        this.status = status;
        this.name = name;
        parent.info(String.format("A %s %s is being created...", name, status.getTarget()));
    }


    @Override
    protected void onPostProcessTarget(AjaxRequestTarget target) {
        super.onPostProcessTarget(target);
        status.refresh();
        if (status.getState() != State.RUNNING) {
            stop();
            parent.remove(this);
        }
        switch (status.getState()) {
            case RUNNING:
                parent.info(String.format("A %s %s is being created...", name, status.getTarget()));
                break;
            case DONE:
                parent.success(String.format("%s %s has been created!", StringUtils.capitalize(name),
                    status.getTarget()));
                onSuccess(target);
                break;
            default:
                parent.error(String.format("%s: %s", status.getState(), status.getReason()));
        }
        target.add(parent);
    }


    public abstract void onSuccess(AjaxRequestTarget target);
}