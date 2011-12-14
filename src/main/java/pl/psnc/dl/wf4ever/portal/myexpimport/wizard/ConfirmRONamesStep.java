/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ConfirmRONamesStep
	extends AbstractImportStep
{

	private static final long serialVersionUID = -3238571883021517707L;


	public ConfirmRONamesStep(ImportModel model)
	{
		super("Confirm RO identifier", null);

		Form< ? > form = new Form<Void>("form");
		RequiredTextField<String> name = new RequiredTextField<String>("roId");
		name.add(new PatternValidator("[\\w]+"));
		form.add(name);
		add(form);
	}
}
