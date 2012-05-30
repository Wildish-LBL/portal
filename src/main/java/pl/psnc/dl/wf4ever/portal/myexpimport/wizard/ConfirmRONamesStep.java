/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.log4j.Logger;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.purl.wf4ever.rosrs.client.common.ROSRService;

import pl.psnc.dl.wf4ever.portal.PortalApplication;

/**
 * Step for providing the RO ID.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ConfirmRONamesStep extends WizardStep {

    /** id. */
    private static final long serialVersionUID = -3238571883021517707L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ConfirmRONamesStep.class);


    /**
     * Constructor.
     * 
     * @param model
     *            import model
     */
    @SuppressWarnings("serial")
    public ConfirmRONamesStep(ImportModel model) {
        super("Confirm RO identifier", null);

        Form<?> form = new Form<Void>("form");
        RequiredTextField<String> name = new RequiredTextField<String>("roId");
        name.add(new IValidator<String>() {

            @Override
            public void validate(IValidatable<String> validatable) {
                try {
                    if (!ROSRService.isRoIdFree(((PortalApplication) getApplication()).getRodlURI(),
                        validatable.getValue())) {
                        validatable.error(new ValidationError().setMessage("This ID is already in use"));
                    }
                } catch (Exception e) {
                    LOG.error(e);
                    // assume it's ok
                }
            }

        });
        form.add(name);
        add(form);
    }
}
