/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;

import pl.psnc.dl.wf4ever.portal.components.annotations.TemplateDropDownChoice;

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
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ConfirmRONamesStep.class);


    /**
     * Constructor.
     * 
     * @param model
     *            import model
     */
    public ConfirmRONamesStep(ImportModel model) {
        super("Confirm RO identifier", null);

        Form<?> form = new Form<Void>("form");
        form.add(new RequiredTextField<String>("roId"));
        TemplateDropDownChoice templates = new TemplateDropDownChoice("template");
        final Label templateDescription = new Label("template.description");
        templateDescription.setOutputMarkupId(true);
        form.add(templates);
        form.add(templateDescription);
        templates.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(templateDescription);
            }
        });

        form.add(new TextField<String>("title"));
        form.add(new TextArea<String>("description"));
        add(form);
    }
}
