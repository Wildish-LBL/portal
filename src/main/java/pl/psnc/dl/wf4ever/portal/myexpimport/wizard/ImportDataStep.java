/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.portal.services.MyExpImportService;

import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;

/**
 * Step of performing the import process.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ImportDataStep extends WizardStep {

    /** id. */
    private static final long serialVersionUID = -2632389547400514998L;

    /** Progress bar refresh interval, in ms. */
    private static final double INTERVAL = 500;


    /**
     * Constructor.
     * 
     * @param model
     *            import model
     */
    public ImportDataStep(final ImportModel model) {
        super("Import data", null);
        setOutputMarkupId(true);
        setComplete(false);

        final ProgressBar progressBar = new ProgressBar("progress-bar", new PropertyModel<Integer>(model,
                "progressInPercent"));
        add(progressBar);
        final Label importStatus = new Label("message", new PropertyModel<String>(model, "message"));
        add(importStatus.setOutputMarkupId(true).setEscapeModelStrings(false));

        final AjaxSelfUpdatingTimerBehavior updater = new AjaxSelfUpdatingTimerBehavior(Duration.milliseconds(INTERVAL)) {

            /** id. */
            private static final long serialVersionUID = 4629392247158141573L;


            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                super.onPostProcessTarget(target);
                target.add(progressBar);
                if (model.getStatus() == ImportStatus.FINISHED || model.getStatus() == ImportStatus.FAILED) {
                    stop(target);
                    importStatus.remove(this);
                    setComplete(true);
                    target.add(ImportDataStep.this.getParent().getParent());
                }
            }
        };
        add(new AjaxButton("go") {

            /** id. */
            private static final long serialVersionUID = -1566765092932052987L;


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }


            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (model.getStatus() == ImportStatus.NOT_STARTED) {
                    PortalApplication app = (PortalApplication) getApplication();
                    MySession session = (MySession) getSession();
                    MyExpImportService.startImport(model, session.getRosrs(), app.getWf2ROService(),
                        session.getMyExpAccessToken(), app.getMyExpConsumerKey(), app.getMyExpConsumerSecret());
                    importStatus.add(updater);
                    target.add(importStatus);

                    this.setEnabled(false);
                    target.add(this);
                }
            }
        }).setEnabled(model.getStatus() == ImportStatus.NOT_STARTED).setOutputMarkupId(true);
    }


    @Override
    protected void onBeforeRender() {
        Application.get().getMarkupSettings().setStripWicketTags(true);
        super.onBeforeRender();
    }


    @Override
    protected void onAfterRender() {
        Application.get().getMarkupSettings().setStripWicketTags(false);
        super.onAfterRender();
    }
}
