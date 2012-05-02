/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.portal.services.MyExpImportService;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class ImportDataStep
	extends AbstractImportStep
{

	private static final long serialVersionUID = -2632389547400514998L;

	private static final double INTERVAL = 500;


	@SuppressWarnings("serial")
	public ImportDataStep(final ImportModel model)
	{
		super("Import data", null);
		setOutputMarkupId(true);
		final Label importStatus = new Label("message", new PropertyModel<String>(model, "message"));
		importStatus.setOutputMarkupId(true);
		importStatus.setEscapeModelStrings(false);
		add(importStatus);
		setComplete(false);

		final AjaxSelfUpdatingTimerBehavior updater = new AjaxSelfUpdatingTimerBehavior(Duration.milliseconds(INTERVAL)) {

			@Override
			protected void onPostProcessTarget(AjaxRequestTarget target)
			{
				super.onPostProcessTarget(target);
				target.appendJavaScript("$(\"#progressbar\").progressbar(\"value\", " + model.getProgressInPercent()
						+ ");");

				if (model.getStatus() == ImportStatus.FINISHED || model.getStatus() == ImportStatus.FAILED) {
					stop();
					importStatus.remove(this);
					setComplete(true);
					target.add(ImportDataStep.this.getParent().getParent());
				}
			}
		};
		add(new AjaxButton("go") {

			@Override
			protected void onError(AjaxRequestTarget target, Form< ? > form)
			{
			}


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				if (model.getStatus() == ImportStatus.NOT_STARTED) {
					PortalApplication app = (PortalApplication) getApplication();
					MyExpImportService.startImport(model, ((PortalApplication) PortalApplication.get()).getRodlURI(),
						MySession.get().getMyExpAccessToken(), MySession.get().getdLibraAccessToken(),
						app.getMyExpConsumerKey(), app.getMyExpConsumerSecret());
					importStatus.add(updater);
					target.add(importStatus);

					this.setEnabled(false);
					target.add(this);
					getRequestCycle().setResponsePage(getPage());
				}
			}
		}).setEnabled(model.getStatus() == ImportStatus.NOT_STARTED).setOutputMarkupId(true);
	}

}
