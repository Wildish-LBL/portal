package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.purl.wf4ever.rosrs.client.Folder;

/**
 * User clicked Transform.
 * 
 * 
 */
public class WorkflowTransormRequestEvent extends AbstractAjaxEvent implements
		AbstractClickAjaxEvent {

	private Folder extractToFolder;
	private Folder scriptsToFolder;
	private Folder nestedRoToFolder;
	private Folder webservicesToFolder;

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            AJAX request target
	 */
	public WorkflowTransormRequestEvent(AjaxRequestTarget target, Folder extractToFolder,
			Folder scriptsToFolder, Folder nestedRoToFolder, Folder webservicesToFolder) {
		super(target);
		this.setExtractToFolder(extractToFolder);
		this.setScriptsToFolder(scriptsToFolder);
		this.setNestedRoToFolder(nestedRoToFolder);
		this.setWebservicesToFolder(webservicesToFolder);
	}

	public Folder getNestedRoToFolder() {
		return nestedRoToFolder;
	}

	public void setNestedRoToFolder(Folder nestedRoToFolder) {
		this.nestedRoToFolder = nestedRoToFolder;
	}

	public Folder getWebservicesToFolder() {
		return webservicesToFolder;
	}

	public void setWebservicesToFolder(Folder webservicesToFolder) {
		this.webservicesToFolder = webservicesToFolder;
	}

	public Folder getScriptsToFolder() {
		return scriptsToFolder;
	}

	public void setScriptsToFolder(Folder scriptsToFolder) {
		this.scriptsToFolder = scriptsToFolder;
	}

	public Folder getExtractToFolder() {
		return extractToFolder;
	}

	public void setExtractToFolder(Folder extractToFolder) {
		this.extractToFolder = extractToFolder;
	}
}
