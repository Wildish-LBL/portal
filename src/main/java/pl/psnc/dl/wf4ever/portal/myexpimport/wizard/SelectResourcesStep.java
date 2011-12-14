/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * @author Piotr Hołubowicz
 *
 */
public class SelectResourcesStep
	extends AbstractImportStep
{

	private static final long serialVersionUID = -7984392838783804920L;

	private Form< ? > customPackIdForm;


	@SuppressWarnings("serial")
	public SelectResourcesStep(final ImportModel model)
	{
		super("Select resources", null);

		final ResourceListPanel filesDiv;
		if (model.getMyExpUser().getFiles().isEmpty()) {
			filesDiv = null;
			add(createUnvisibleDiv("filesDiv"));
		}
		else {
			filesDiv = new ResourceListPanel("filesDiv", "Files", model.getMyExpUser().getFiles(),
					model.getSelectedFiles());
			add(filesDiv);
		}
		final ResourceListPanel workflowsDiv;
		if (model.getMyExpUser().getWorkflows().isEmpty()) {
			workflowsDiv = null;
			add(createUnvisibleDiv("workflowsDiv"));
		}
		else {
			workflowsDiv = new ResourceListPanel("workflowsDiv", "Workflows", model.getMyExpUser().getWorkflows(),
					model.getSelectedWorkflows());
			add(workflowsDiv);
		}
		final ResourceListPanel packsDiv;
		if (model.getMyExpUser().getPacks().isEmpty()) {
			packsDiv = null;
			add(createUnvisibleDiv("packsDiv"));
		}
		else {
			packsDiv = new ResourceListPanel("packsDiv", "Packs", model.getMyExpUser().getPacks(),
					model.getSelectedPacks());
			add(packsDiv);
		}

		customPackIdForm = new Form<Void>("form");
		TextField<String> customPackId = new TextField<String>("customPackId");
		customPackId.add(new PatternValidator("[0-9]+"));
		customPackIdForm.add(customPackId);
		add(customPackIdForm);

		add(new IFormValidator() {

			@Override
			public void validate(Form< ? > form)
			{
				if (filesDiv != null)
					filesDiv.commit();
				if (workflowsDiv != null)
					workflowsDiv.commit();
				if (packsDiv != null)
					packsDiv.commit();
				customPackIdForm.process(null);
				if (!model.isValid()) {
					error("You must select at least one resource.");
				}
			}


			@Override
			public FormComponent< ? >[] getDependentFormComponents()
			{
				return null;
			}
		});
	}


	/**
	 * @return
	 */
	private WebMarkupContainer createUnvisibleDiv(String id)
	{
		WebMarkupContainer div = new WebMarkupContainer(id);
		div.setVisible(false);
		return div;
	}

}