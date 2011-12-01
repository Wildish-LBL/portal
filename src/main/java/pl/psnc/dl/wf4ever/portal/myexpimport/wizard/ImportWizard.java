package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.portal.myexpimport.model.User;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.portal.pages.MyRosPage;

public class ImportWizard
	extends Wizard
{

	private static final long serialVersionUID = -8520850154339581229L;


	public ImportWizard(String id, User user)
	{
		super(id, false);
		final ImportModel importModel = new ImportModel(user);
		setDefaultModel(new CompoundPropertyModel<ImportModel>(importModel));

		WizardModel wizardModel = new WizardModel() {

			private static final long serialVersionUID = 3739070705866873527L;


			@Override
			public boolean isPreviousAvailable()
			{
				return importModel.getStatus() != ImportStatus.RUNNING;
			}


			@Override
			public boolean isNextAvailable()
			{
				return importModel.getStatus() != ImportStatus.RUNNING;
			}

		};
		wizardModel.add(new StartImportStep());
		wizardModel.add(new ChooseWorkspaceStep(importModel));
		wizardModel.add(new SelectResourcesStep(importModel));
		wizardModel.add(new ConfirmRONamesStep(importModel));
		wizardModel.add(new ImportDataStep(importModel));
		wizardModel.add(new SummaryStep(importModel));
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

}