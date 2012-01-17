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

	private WorkspaceType workspaceType = WorkspaceType.EXISTING;

	private final List<FileHeader> selectedFiles;

	private final List<WorkflowHeader> selectedWorkflows;

	private final List<PackHeader> selectedPacks;

	private String roId;

	private int progressInPercent = 0;

	private String customPackId;


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
	 * @param message
	 *            the message to set
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
	 * 
	 * @param messages
	 */
	public void setMessages(List<String> messages)
	{
		// do nothing
	}


	/**
	 * @return the status
	 */
	public ImportStatus getStatus()
	{
		return status;
	}


	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(ImportStatus status)
	{
		this.status = status;
	}


	/**
	 * @return the workspaceType
	 */
	public WorkspaceType getWorkspaceType()
	{
		return workspaceType;
	}


	/**
	 * @param workspaceType
	 *            the workspaceType to set
	 */
	public void setWorkspaceType(WorkspaceType workspaceType)
	{
		this.workspaceType = workspaceType;
	}


	public boolean isValid()
	{
		return !selectedFiles.isEmpty() || !selectedWorkflows.isEmpty() || !selectedPacks.isEmpty()
				|| customPackId != null;
	}


	/**
	 * @return the roId
	 */
	public String getRoId()
	{
		return roId;
	}


	/**
	 * @param roId
	 *            the roName to set
	 */
	public void setRoId(String roId)
	{
		this.roId = roId;
	}


	/**
	 * @return the progressInPercent
	 */
	public int getProgressInPercent()
	{
		return progressInPercent;
	}


	/**
	 * @param progressInPercent
	 *            the progressInPercent to set
	 */
	public void setProgressInPercent(int progressInPercent)
	{
		this.progressInPercent = progressInPercent;
	}


	/**
	 * @return the customPackId
	 */
	public String getCustomPackId()
	{
		return customPackId;
	}


	/**
	 * @param customPackId
	 *            the customPackId to set
	 */
	public void setCustomPackId(String customPackId)
	{
		this.customPackId = customPackId;
	}

}
