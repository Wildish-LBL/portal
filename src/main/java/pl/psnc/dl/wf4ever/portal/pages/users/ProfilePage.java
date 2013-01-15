package pl.psnc.dl.wf4ever.portal.pages.users;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.common.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.User;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

/**
 * This class allows to manage your account in RODL and unregister.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@AuthorizeInstantiation("USER")
public class ProfilePage extends Base {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ProfilePage.class);


    /**
     * Constructor called by Wicket with an auth response (since the response has parameters associated with it... LOTS
     * of them!). And, by the way, the auth response is the Request for this class (not to be confusing).
     * 
     * @param pageParameters
     *            The request parameters (which are the response parameters from the OP).
     */
    @SuppressWarnings("serial")
    public ProfilePage(PageParameters pageParameters) {
        super(pageParameters);

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        add(new BookmarkablePageLink<Void>("profile", ProfilePage.class));
        add(new BookmarkablePageLink<Void>("tokens", AccessTokensPage.class));
        add(new BookmarkablePageLink<Void>("generate", GenerateAccessTokenPage.class));

        final User user = ((MySession) getSession()).getUser();

        Form<User> form = new Form<User>("form", new CompoundPropertyModel<User>(user));
        form.setOutputMarkupId(true);
        add(form);

        form.add(new Label("URI"));

        form.add(new AjaxButton("unregister") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                PortalApplication app = ((PortalApplication) getApplication());
                try {
                    UserManagementService.deleteUser(app.getRodlURI(), app.getAdminToken(), user.getURI().toString());
                    getSession().info("Account has been deleted.");
                    throw new RestartResponseException(getApplication().getHomePage());
                } catch (Exception e) {
                    LOG.error("Could not delete an account in RODL", e);
                    error(e.getMessage() != null ? e.getMessage() : "Unknown error");
                }
                target.add(feedbackPanel);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        }).setOutputMarkupId(true);
    }
}
