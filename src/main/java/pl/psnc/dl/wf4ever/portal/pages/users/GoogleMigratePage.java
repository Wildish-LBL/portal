package pl.psnc.dl.wf4ever.portal.pages.users;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.pages.TemplatePage;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

/**
 * Decide whether to migrate the OpenID.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class GoogleMigratePage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = -8975579933617712699L;

    /** UMA authorization URL. */
    private static String authorizationUrl;

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters
     */
    @SuppressWarnings("serial")
    public GoogleMigratePage(PageParameters pageParameters) {
        super(pageParameters);

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        MySession.get().setUpdateURI(true);

        Form<?> form = new Form<Void>("migrateForm");
        add(form);
        form.add(new Button("cancel") {

            @Override
            public void onSubmit() {
                super.onSubmit();
                throw new RestartResponseException(getApplication().getHomePage());
            }
        });
        Button migrateButton = new Button("migrate") {

            @Override
            public void onSubmit() {
                super.onSubmit();
                throw new RedirectToUrlException(authorizationUrl);
            }
        };
        form.add(migrateButton);

        if (authorizationUrl == null) {
            error("UMA authorization URL has not been set.");
            migrateButton.setEnabled(false);
        }
    }


    public static void setAuthorizationURL(String authorizationUrl) {
        GoogleMigratePage.authorizationUrl = authorizationUrl;
    }

}
