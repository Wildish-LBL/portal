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
 * @author Piotr Hołubowicz
 *
 */
public class ImportModel
	implements Serializable
{

	public enum ImportStatus {
		NOT_STARTED, RUNNING, FINISHED
	}

	public enum WorkspaceType {
		EXISTING, NEW
	}

	private static final long serialVersionUID = -6654329540413067819L;

	private final User myExpUser;

	private String message = "Ready";

	private List<String> messages = new ArrayList<String>();

	private ImportStatus status = ImportStatus.NOT_STARTED;

	private boolean mergeROs = true;

	private WorkspaceType workspaceType = WorkspaceType.EXISTING;

	private String existingWorkspaceId;

	private String newWorkspaceId;

	private final List<FileHeader> selectedFiles;

	private final List<WorkflowHeader> selectedWorkflows;

	private final List<PackHeader> selectedPacks;

	private String roName;

	private int progressInPercent = 0;


	public ImportModel(User user)
	{
		super();
		this.myExpUser = user;
		this.selectedFiles = new ArrayList<FileHeader>();
		this.selectedWorkflows = new ArrayList<WorkflowHeader>();
		this.selectedPacks = new ArrayList<PackHeader>();
	}


	public List<FileHeader> getSelectedFiles()
	{
		return selectedFiles;
	}


	public List<WorkflowHeader> getSelectedWorkflows()
	{
		return selectedWorkflows;
	}


	public List<PackHeader> getSelectedPacks()
	{
		return selectedPacks;
	}


	/**
	 * @return the myExpUser
	 */
	public User getMyExpUser()
	{
		return myExpUser;
	}


	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
		if (messages == null)
			messages = new ArrayList<String>();
		messages.add(message);
	}


	/**
	 * @return the messages
	 */
	public String getMessages()
	{
		return StringUtils.join(messages, "\r\n");
	}


	/**
	 * This method doesn't do anything.
	 * @param messages
	 */
	public void setMessages(List<String> messages)
	{
		// do nothing
	}


	/**
	 * @return the mergeROs
	 */
	public boolean isMergeROs()
	{
		return mergeROs;
	}


	/**
	 * @param mergeROs the mergeROs to set
	 */
	public void setMergeROs(boolean mergeROs)
	{
		this.mergeROs = mergeROs;
	}


	/**
	 * @return the status
	 */
	public ImportStatus getStatus()
	{
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(ImportStatus status)
	{
		this.status = status;
	}


	/**
	 * @return the workspaceId
	 */
	public String getNewWorkspaceId()
	{
		return newWorkspaceId;
	}


	/**
	 * @param workspaceId the workspaceId to set
	 */
	public void setNewWorkspaceId(String workspaceId)
	{
		this.newWorkspaceId = workspaceId;
	}


	/**
	 * @return the existingWorkspaceId
	 */
	public String getExistingWorkspaceId()
	{
		return existingWorkspaceId;
	}


	/**
	 * @param existingWorkspaceId the existingWorkspaceId to set
	 */
	public void setExistingWorkspaceId(String existingWorkspaceId)
	{
		this.existingWorkspaceId = existingWorkspaceId;
	}


	public String getWorkspaceId()
	{
		switch (getWorkspaceType()) {
			case EXISTING:
				return getExistingWorkspaceId();
			case NEW:
				return getNewWorkspaceId();
			default:
				return null;
		}
	}


	/**
	 * @return the workspaceType
	 */
	public WorkspaceType getWorkspaceType()
	{
		return workspaceType;
	}


	/**
	 * @param workspaceType the workspaceType to set
	 */
	public void setWorkspaceType(WorkspaceType workspaceType)
	{
		this.workspaceType = workspaceType;
	}


	public boolean isValid()
	{
		return !selectedFiles.isEmpty() || !selectedWorkflows.isEmpty() || !selectedPacks.isEmpty();
	}


	/**
	 * @return the roName
	 */
	public String getRoName()
	{
		return roName;
	}


	/**
	 * @param roName the roName to set
	 */
	public void setRoName(String roName)
	{
		this.roName = roName;
	}


	/**
	 * @return the progressInPercent
	 */
	public int getProgressInPercent()
	{
		return progressInPercent;
	}


	/**
	 * @param progressInPercent the progressInPercent to set
	 */
	public void setProgressInPercent(int progressInPercent)
	{
		this.progressInPercent = progressInPercent;
	}

}
