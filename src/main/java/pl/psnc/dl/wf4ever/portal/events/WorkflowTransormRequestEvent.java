package pl.psnc.dl.wf4ever.portal.events;

import java.net.URI;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User clicked Transform.
 * 
 * 
 */
public class WorkflowTransormRequestEvent extends AbstractAjaxEvent implements
		AbstractClickAjaxEvent {

	private URI extractToFolderUri;
	private URI scriptsToFolderUri;
	private URI nestedRoToFolderUri;
	private URI webservicesToFolderUri;

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            AJAX request target
	 */
	public WorkflowTransormRequestEvent(AjaxRequestTarget target, URI extractToFolder,
			URI scriptsToFolder, URI nestedRoToFolder, URI webservicesToFolder) {
		super(target);
		this.setExtractToFolderUri(extractToFolder);
		this.setScriptsToFolderUri(scriptsToFolder);
		this.setNestedRoToFolderUri(nestedRoToFolder);
		this.setWebservicesToFolderUri(webservicesToFolder);
	}

	public URI getExtractToFolderUri() {
		return extractToFolderUri;
	}

	public void setExtractToFolderUri(URI extractToFolderUri) {
		this.extractToFolderUri = extractToFolderUri;
	}

	public URI getScriptsToFolderUri() {
		return scriptsToFolderUri;
	}

	public void setScriptsToFolderUri(URI scriptsToFolderUri) {
		this.scriptsToFolderUri = scriptsToFolderUri;
	}

	public URI getNestedRoToFolderUri() {
		return nestedRoToFolderUri;
	}

	public void setNestedRoToFolderUri(URI nestedRoToFolderUri) {
		this.nestedRoToFolderUri = nestedRoToFolderUri;
	}

	public URI getWebservicesToFolderUri() {
		return webservicesToFolderUri;
	}

	public void setWebservicesToFolderUri(URI webservicesToFolderUri) {
		this.webservicesToFolderUri = webservicesToFolderUri;
	}

}
