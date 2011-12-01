/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.extensions.wizard.WizardStep;

/**
 * @author piotrhol
 *
 */
public abstract class AbstractImportStep
	extends WizardStep
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8362076393015706588L;


	public AbstractImportStep(String title, String summary)
	{
		super(title, summary);

	}

}
