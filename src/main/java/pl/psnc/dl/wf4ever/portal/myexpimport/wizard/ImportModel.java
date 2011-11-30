package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;

import pl.psnc.dl.wf4ever.portal.myexpimport.model.FileHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.PackHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.User;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.WorkflowHeader;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ImportModel
	extends DynamicWizardModel
{

	public enum ImportStatus {
		NOT_STARTED, RUNNING, PAUSED, FINISHED
	}

	public enum WorkspaceType {
		EXISTING, NEW
	}

	private static final long serialVersionUID = -6654329540413067819L;

	private final List<ResearchObject> researchObjects;

	private final User myExpUser;

	private String message = "Import has not started";

	private List<String> messages = new ArrayList<String>();

	private ImportStatus status = ImportStatus.NOT_STARTED;

	private boolean mergeROs = true;

	private WorkspaceType workspaceType = WorkspaceType.EXISTING;

	private String existingWorkspaceId;

	private String newWorkspaceId;

	//HACK this is copied from DynamicWizardModel because it is private not protected.
	/**
	 * Remember the first step for resetting the wizard.
	 */
	private IDynamicWizardStep startStep;


	public ImportModel(User user)
	{
		super(null);
		this.myExpUser = user;
		this.researchObjects = new ArrayList<ResearchObject>();
	}


	/**
	 * @return the researchObjects
	 */
	public List<ResearchObject> getResearchObjects()
	{
		return researchObjects;
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


	public List<FileHeader> getImportedFiles()
	{
		List<FileHeader> list = new ArrayList<FileHeader>();
		for (ResearchObject ro : researchObjects) {
			list.addAll(ro.getFiles());
		}
		return list;
	}


	public List<WorkflowHeader> getImportedWorkflows()
	{
		List<WorkflowHeader> list = new ArrayList<WorkflowHeader>();
		for (ResearchObject ro : researchObjects) {
			list.addAll(ro.getWorkflows());
		}
		return list;
	}


	public List<PackHeader> getImportedPacks()
	{
		List<PackHeader> list = new ArrayList<PackHeader>();
		for (ResearchObject ro : researchObjects) {
			list.addAll(ro.getPacks());
		}
		return list;
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


	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardModel#reset()
	 */
	@Override
	public void reset()
	{
		setActiveStep(startStep);
	}


	/**
	 * @param startStep the startStep to set
	 */
	public void setStartStep(IDynamicWizardStep startStep)
	{
		this.startStep = startStep;
	}

}
