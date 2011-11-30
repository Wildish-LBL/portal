/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * @author piotrhol
 *
 */
public abstract class AbstractImportStep
	extends DynamicWizardStep
{

	private static final String[] stepIDs = { "StartImportStep", "ChooseWorkspaceStep", "DefineROMetastep",
			"SelectResourcesStep", "ConfirmRONamesStep", "ImportDataStep", "SummaryStep"};

	private static final String[] defineROMetastepIDs = { "SelectResourcesStep", "ConfirmRONamesStep"};

	/**
	 * 
	 */
	private static final long serialVersionUID = -8362076393015706588L;


	public AbstractImportStep(IDynamicWizardStep previousStep, String title, String summary)
	{
		super(previousStep, title, summary);

		for (String stepID : stepIDs) {
			Label label = new Label(stepID);
			if (stepID.equals(this.getClass().getName())) {
				label.add(new AttributeModifier("class", new Model<String>("selectedStep")));
			}
			if (stepID.equals("DefineROMetastep")
					&& ArrayUtils.contains(defineROMetastepIDs, this.getClass().getName())) {
				label.add(new AttributeModifier("class", new Model<String>("selectedStep")));
			}
			add(label);
		}
	}

}
