package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * Indicators of the process progress.
 * 
 * @author piotrekhol
 * 
 */
public class CreateFromZipProgressModel implements Serializable {

    /**
     * Thread state set in the run() method.
     * 
     * @author piotrekhol
     * 
     */
    public enum State {
        /** Thread is running. */
        RUNNING,
        /** Thread finished successfully or not. */
        TERMINATED
    }


    /** id. */
    private static final long serialVersionUID = 4410500942718651694L;

    /** Is the thread running or not. */
    private State threadState = State.RUNNING;

    /** Number of completed steps or null if unknown yet. */
    private Integer complete = null;

    /** Number of total steps or null if unknown yet. */
    private Integer total = null;

    /** The output for the user. */
    private StringBuilder outputStringBuilder = new StringBuilder();

    /** The URI of the created RO. */
    private URI roUri = null;

    /** Time when the thread started. */
    private DateTime startTime;

    /** Estimated time when the thread finishes. */
    private DateTime endTime;


    public synchronized State getThreadState() {
        return threadState;
    }


    public synchronized void setThreadState(State threadState) {
        this.threadState = threadState;
    }


    public synchronized Integer getComplete() {
        return complete;
    }


    /**
     * Increment the complete steps counter.
     */
    public synchronized void incrementComplete() {
        this.complete++;
    }


    public synchronized Integer getTotal() {
        return total;
    }


    /**
     * Reset the step counter.
     * 
     * @param total
     *            total steps
     */
    public synchronized void setTotal(Integer total) {
        this.complete = 0;
        this.total = total;
    }


    public synchronized String getOutputString() {
        return outputStringBuilder.toString();
    }


    /**
     * Append text to the string builder.
     * 
     * @param text
     *            text to append
     */
    public synchronized void appendToOutput(String text) {
        outputStringBuilder.append(text);
    }


    public synchronized URI getRoUri() {
        return roUri;
    }


    public synchronized void setRoUri(URI roUri) {
        this.roUri = roUri;
    }


    public synchronized Duration getTimeElapsed() {
        return startTime != null ? new Interval(startTime, DateTime.now()).toDuration() : null;
    }


    /**
     * Get an estimate of time remaining to finish.
     * 
     * @return a Duration or null
     */
    public synchronized Duration getTimeRemaining() {
        if (endTime != null) {
            DateTime now = DateTime.now();
            if (now.isAfter(endTime)) {
                return new Duration(0);
            } else {
                return new Interval(DateTime.now(), endTime).toDuration();
            }
        } else {
            return null;
        }
    }


    /**
     * Get elapsed time as string hh:mm:ss.
     * 
     * @return formatted time or null
     */
    public synchronized String getTimeElapsedFormatted() {
        Duration duration = getTimeElapsed();
        if (duration == null) {
            return null;
        }
        Period period = duration.toPeriod();
        return String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
    }


    /**
     * Get remaining time as string hh:mm:ss.
     * 
     * @return formatted time or null
     */
    public synchronized String getTimeRemainingFormatted() {
        Duration duration = getTimeRemaining();
        if (duration == null) {
            return null;
        }
        Period period = duration.toPeriod();
        return String.format("%02d:%02d:%02d", period.getHours(), period.getMinutes(), period.getSeconds());
    }


    public DateTime getStartTime() {
        return startTime;
    }


    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }


    public DateTime getEndTime() {
        return endTime;
    }


    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

}
