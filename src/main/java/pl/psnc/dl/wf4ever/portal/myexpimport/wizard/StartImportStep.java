/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.markup.html.basic.Label;

/**
 * @author Piotr Hołubowicz
 *
 */
public class StartImportStep
	extends AbstractImportStep
{

	private static final long serialVersionUID = 4637256013660809942L;


	public StartImportStep()
	{
		super("Start", null);

		add(new Label("myExpUser.name"));
		add(new Label("myExpUser.packs.size"));
		add(new Label("myExpUser.workflows.size"));
		add(new Label("myExpUser.files.size"));
	}

}
