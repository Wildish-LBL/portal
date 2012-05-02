/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class ConfirmRONamesStep
	extends AbstractImportStep
{

	private static final long serialVersionUID = -3238571883021517707L;

	private static final Logger log = Logger.getLogger(ConfirmRONamesStep.class);


	@SuppressWarnings("serial")
	public ConfirmRONamesStep(ImportModel model)
	{
		super("Confirm RO identifier", null);

		Form< ? > form = new Form<Void>("form");
		RequiredTextField<String> name = new RequiredTextField<String>("roId");
		name.add(new IValidator<String>() {

			@Override
			public void validate(IValidatable<String> validatable)
			{
				try {
					if (!ROSRService.isRoIdFree(((PortalApplication) getApplication()).getRodlURI(),
						validatable.getValue())) {
						validatable.error(new ValidationError().setMessage("This ID is already in use"));
					}
				}
				catch (Exception e) {
					log.error(e);
					// assume it's ok
				}
			}

		});
		form.add(name);
		add(form);
	}
}
