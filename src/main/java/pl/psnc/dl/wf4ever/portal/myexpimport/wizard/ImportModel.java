package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

/**
 * 
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import pl.psnc.dl.wf4ever.portal.myexpimport.model.FileHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.PackHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.User;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.WorkflowHeader;

/**
 * The import model, which contains all the import settings and status.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ImportModel implements Serializable {

    /** Import (data copying) process status. */
    public enum ImportStatus {
        /** not started. */
        NOT_STARTED,
        /** running. */
        RUNNING,
        /** finished successfully. */
        FINISHED,
        /** failed. */
        FAILED
    }


    /** What myExperiment data will be imported. */
    public enum ImportedData {
        /** Personal data of a myExperiment user. */
        PERSONAL_ITEMS,
        /** A public pack. */
        PUBLIC_PACK,
        /** A public workflow. */
        PUBLIC_WORKFLOW
    }


    /** id. */
    private static final long serialVersionUID = -6654329540413067819L;

    /** myExperiment access token holder if the resources are personal. */
    private User myExpUser;

    /** Current message for the user. */
    private String message = "Ready";

    /** A list of all messages. */
    private List<String> messages = new ArrayList<String>();

    /** Data import status. */
    private ImportStatus status = ImportStatus.NOT_STARTED;

    /** Imported data type. */
    private ImportedData importedData = ImportedData.PUBLIC_PACK;

    /** Selected personal files. */
    private final List<FileHeader> selectedPersonalFiles;

    /** Selected personal workflows. */
    private final List<WorkflowHeader> selectedPersonalWorkflows;

    /** Selected personal packs. */
    private final List<PackHeader> selectedPersonalPacks;

    /** Public pack id. */
    private String publicPackId;

    /** Public workflow id. */
    private String publicWorkflowId;

    /** New RO id. */
    private String roId;

    /** Data import progress, 0-100. */
    private int progressInPercent = 0;


    /**
     * Constructor.
     * 
     */
    public ImportModel() {
        super();
        this.selectedPersonalFiles = new ArrayList<FileHeader>();
        this.selectedPersonalWorkflows = new ArrayList<WorkflowHeader>();
        this.selectedPersonalPacks = new ArrayList<PackHeader>();
    }


    public ImportedData getImportedData() {
        return importedData;
    }


    public void setImportedData(ImportedData importedData) {
        this.importedData = importedData;
    }


    public List<FileHeader> getSelectedFiles() {
        return selectedPersonalFiles;
    }


    public List<WorkflowHeader> getSelectedWorkflows() {
        return selectedPersonalWorkflows;
    }


    public List<PackHeader> getSelectedPacks() {
        return selectedPersonalPacks;
    }


    public String getPublicPackId() {
        return publicPackId;
    }


    public void setPublicPackId(String customPackId) {
        this.publicPackId = customPackId;
    }


    public String getPublicWorkflowId() {
        return publicWorkflowId;
    }


    public void setPublicWorkflowId(String customPackId) {
        this.publicWorkflowId = customPackId;
    }


    public User getMyExpUser() {
        return myExpUser;
    }


    public void setMyExpUser(User myExpUser) {
        this.myExpUser = myExpUser;
    }


    public String getMessage() {
        return message;
    }


    /**
     * Set the current message.
     * 
     * @param message
     *            message
     */
    public void setMessage(String message) {
        this.message = message;
        if (messages == null) {
            messages = new ArrayList<String>();
        }
        messages.add(message);
    }


    public String getMessages() {
        return StringUtils.join(messages, "\r\n");
    }


    /**
     * This method doesn't do anything.
     * 
     * @param messages
     *            messages
     */
    public void setMessages(List<String> messages) {
        // do nothing
    }


    public ImportStatus getStatus() {
        return status;
    }


    public void setStatus(ImportStatus status) {
        this.status = status;
    }


    public boolean isValid() {
        return !selectedPersonalFiles.isEmpty() || !selectedPersonalWorkflows.isEmpty()
                || !selectedPersonalPacks.isEmpty() || publicPackId != null || publicWorkflowId != null;
    }


    public String getRoId() {
        return roId;
    }


    public void setRoId(String roId) {
        this.roId = roId;
    }


    public int getProgressInPercent() {
        return progressInPercent;
    }


    public void setProgressInPercent(int progressInPercent) {
        this.progressInPercent = progressInPercent;
    }

}
