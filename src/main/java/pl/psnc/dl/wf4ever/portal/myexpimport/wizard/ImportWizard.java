package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.wizard.Wizard;

import pl.psnc.dl.wf4ever.portal.pages.MyRosPage;

public class ImportWizard
	extends Wizard
{

	private static final long serialVersionUID = -8520850154339581229L;


	public ImportWizard(String id, ImportModel wizardModel)
	{
		super(id, false);
		wizardModel.addListener(this);
		wizardModel.setCancelVisible(false);
		init(wizardModel);
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