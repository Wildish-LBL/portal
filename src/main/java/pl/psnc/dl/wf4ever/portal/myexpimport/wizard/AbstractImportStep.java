/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardStep;
import org.apache.wicket.extensions.wizard.dynamic.IDynamicWizardStep;
import org.apache.wicket.model.IModel;

/**
 * @author piotrhol
 *
 */
public abstract class AbstractImportStep
	extends DynamicWizardStep
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8362076393015706588L;


	public AbstractImportStep(IDynamicWizardStep previousStep, String title, String summary, IModel< ? > model)
	{
		super(previousStep, title, summary, model);
	}

}
