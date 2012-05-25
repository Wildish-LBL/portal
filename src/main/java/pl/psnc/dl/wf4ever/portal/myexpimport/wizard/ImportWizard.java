package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.portal.myexpimport.model.User;
import pl.psnc.dl.wf4ever.portal.pages.my.MyRosPage;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

public class ImportWizard
	extends Wizard
{

	private static final long serialVersionUID = -8520850154339581229L;


	public ImportWizard(String id, User user)
	{
		super(id, false);
		final ImportModel importModel = new ImportModel(user);
		setDefaultModel(new CompoundPropertyModel<ImportModel>(importModel));

		WizardModel wizardModel = new WizardModel();
		wizardModel.add(new StartImportStep());
		//		wizardModel.add(new ChooseWorkspaceStep(importModel));
		wizardModel.add(new SelectResourcesStep(importModel));
		wizardModel.add(new ConfirmRONamesStep(importModel));
		wizardModel.add(new ImportDataStep(importModel));
		//		wizardModel.add(new SummaryStep(importModel));
		wizardModel.setCancelVisible(false);

		init(wizardModel);
		getForm().add(new TocPanel("toc", wizardModel));
		setOutputMarkupId(true);
	}


	@Override
	public void onFinish()
	{
		throw new RestartResponseException(MyRosPage.class);
	}


	@Override
	protected Component newButtonBar(String id)
	{
		return new ImportButtonBar(id, this);
	}


	@Override
	protected Component newFeedbackPanel(String id)
	{
		return new MyFeedbackPanel(id, new ContainerFeedbackMessageFilter(this));
	}

}