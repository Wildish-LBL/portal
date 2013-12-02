package pl.psnc.dl.wf4ever.portal.modals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.events.WorkflowTransformClickedEvent;

/**
 * A modal for adding resources to the RO.
 * 
 * 
 */
public class TransformROModal extends AbstractModal {

	/** id. */
	private static final long serialVersionUID = 2193789648186156745L;
	private boolean scriptsCheckBoxState = true;
	private boolean nestedWfCheckboxState = true;
	private boolean webservicesCheckboxState = true;
	private AjaxCheckBox checkBoxScripts;
	private AjaxCheckBox checkBoxNestedWf;
	private AjaxCheckBox checkBoxWebsServices;
	private IModel<ResearchObject> resarchObjectModel;
	private List<String> extractToFoldersList;
	private List<String> scriptsToFoldersList;
	private List<String> nestedWfToFoldersList;
	private List<String> webservicesToFoldersList;
	private String selectedExractTo;
	private String selectedScriptsTo;
	private String selectedNestedWfTo;
	private String selectedWebservicesTo;
	private static final String CHOOSE = "Choose folder";
	private DropDownChoice<String> dropDownChoiceExtractTo;
	private DropDownChoice<String> dropDownChoiceScriptsTo;
	private DropDownChoice<String> dropDownChoiceNestedWfTo;
	private DropDownChoice<String> dropDownChoiceWebservicesTo;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            wicket id
	 * @param toDelete
	 *            ROs to delete
	 */
	public TransformROModal(String id, IModel<ResearchObject> researchObjectModel) {
		super(id, "delete-ro-modal", "Annotate & Transform");
		extractToFoldersList = new ArrayList<String>();
		scriptsToFoldersList = new ArrayList<String>();
		nestedWfToFoldersList = new ArrayList<String>();
		webservicesToFoldersList = new ArrayList<String>();

		for (URI key : researchObjectModel.getObject().getFolders().keySet()) {
			String path = researchObjectModel.getObject().getFolder(key).getPath().toString();
			extractToFoldersList.add(path);
			scriptsToFoldersList.add(path);
			nestedWfToFoldersList.add(path);
			webservicesToFoldersList.add(path);
			if (path.equals("content/")) {
				selectedExractTo = path;
			} else if (path.equals("config/scripts/")) {
				selectedScriptsTo = path;
			} else if (path.equals("wf/nested/")) {
				selectedNestedWfTo = path;
			} else if (path.equals("config/services")) {
				selectedWebservicesTo = path;
			}

		}

		if (selectedExractTo == null) {
			selectedExractTo = CHOOSE;
			extractToFoldersList.add(0, CHOOSE);
		}
		if (selectedScriptsTo == null) {
			selectedScriptsTo = CHOOSE;
			scriptsToFoldersList.add(0, CHOOSE);
		}
		if (selectedNestedWfTo == null) {
			selectedNestedWfTo = CHOOSE;
			nestedWfToFoldersList.add(0, CHOOSE);
		}
		if (selectedWebservicesTo == null) {
			selectedWebservicesTo = CHOOSE;
			webservicesToFoldersList.add(0, CHOOSE);
		}

		this.resarchObjectModel = researchObjectModel;

		checkBoxScripts = new AjaxCheckBox("checkBoxScripts", new PropertyModel<Boolean>(this,
				"scriptsCheckBoxState")) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// nth special
			}
		};

		checkBoxNestedWf = new AjaxCheckBox("checkBoxNestedWf", new PropertyModel<Boolean>(this,
				"nestedWfCheckboxState")) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				// nth special

			}
		};

		checkBoxWebsServices = new AjaxCheckBox("checkBoxWebsServices", new PropertyModel<Boolean>(
				this, "webservicesCheckboxState")) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				int a = 3;
				int b = a;
			}
		};

		modal.add(checkBoxScripts);
		modal.add(checkBoxNestedWf);
		modal.add(checkBoxWebsServices);

		dropDownChoiceExtractTo = new DropDownChoice<String>("extractToFoldersList",
				new PropertyModel<String>(this, "selectedExractTo"), extractToFoldersList);
		dropDownChoiceScriptsTo = new DropDownChoice<String>("scriptsToFoldersList",
				new PropertyModel<String>(this, "selectedScriptsTo"), scriptsToFoldersList);
		dropDownChoiceNestedWfTo = new DropDownChoice<String>("nestedWfToFoldersList",
				new PropertyModel<String>(this, "selectedNestedWfTo"), nestedWfToFoldersList);
		dropDownChoiceWebservicesTo = new DropDownChoice<String>("webservicesToFoldersList",
				new PropertyModel<String>(this, "selectedWebservicesTo"), webservicesToFoldersList);

		modal.add(dropDownChoiceExtractTo);
		modal.add(dropDownChoiceScriptsTo);
		modal.add(dropDownChoiceNestedWfTo);
		modal.add(dropDownChoiceWebservicesTo);

	}

	@Override
	public void onOk(AjaxRequestTarget target) {
		send(getPage(), Broadcast.BREADTH, new WorkflowTransformClickedEvent(target));
		hide(target);
	}

	public boolean isScriptsCheckBoxState() {
		return scriptsCheckBoxState;
	}

	public void setScriptsCheckBoxState(boolean scriptsCheckBoxState) {
		this.scriptsCheckBoxState = scriptsCheckBoxState;
	}

	public boolean isNestedWfCheckboxState() {
		return nestedWfCheckboxState;
	}

	public void setNestedWfCheckboxState(boolean nestedWfCheckboxState) {
		this.nestedWfCheckboxState = nestedWfCheckboxState;
	}

	public boolean isWebservicesCheckboxState() {
		return webservicesCheckboxState;
	}

	public void setWebservicesCheckboxState(boolean webservicesCheckboxState) {
		this.webservicesCheckboxState = webservicesCheckboxState;
	}

}
