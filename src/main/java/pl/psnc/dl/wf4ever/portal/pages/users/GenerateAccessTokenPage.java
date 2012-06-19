package pl.psnc.dl.wf4ever.portal.pages.users;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.common.users.OAuthClient;
import org.purl.wf4ever.rosrs.client.common.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.TemplatePage;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

/**
 * This page presents a list of access tokens of the current user.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@AuthorizeInstantiation("USER")
public class GenerateAccessTokenPage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(GenerateAccessTokenPage.class);

    /** The selected out of band client. */
    private OAuthClient oobClient;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters
     */
    @SuppressWarnings("serial")
    public GenerateAccessTokenPage(PageParameters pageParameters) {
        super(pageParameters);

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        add(new BookmarkablePageLink<Void>("profile", ProfilePage.class));
        add(new BookmarkablePageLink<Void>("tokens", AccessTokensPage.class));
        add(new BookmarkablePageLink<Void>("generate", GenerateAccessTokenPage.class));

        PortalApplication app = ((PortalApplication) getApplication());

        List<OAuthClient> oobClients = null;
        try {
            oobClients = UserManagementService.getClients(app.getRodlURI(), app.getAdminToken());
            Iterator<OAuthClient> i = oobClients.iterator();
            while (i.hasNext()) {
                OAuthClient client = i.next();
                if (!OAuthClient.OOB.equals(client.getRedirectionURI())) {
                    i.remove();
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            error(e.getMessage());
            oobClients = new ArrayList<OAuthClient>();
        }

        Form<?> form2 = new Form<Void>("form2") {

            @Override
            protected void onSubmit() {
                super.onSubmit();
                PageParameters params = new PageParameters();
                params.add("clientId", oobClient.getClientId());
                params.add("clientName", oobClient.getName());
                throw new RestartResponseException(OOBAccessTokenPage.class, params);
            }
        };
        add(form2);
        final DropDownChoice<OAuthClient> oobClientList = new DropDownChoice<OAuthClient>("oobClients",
                new PropertyModel<OAuthClient>(this, "oobClient"), oobClients);
        form2.add(oobClientList);

        if (oobClients.isEmpty()) {
            oobClients.add(new OAuthClient(null, "No OOB applications available", null));
            oobClientList.setEnabled(false);
        }
        this.oobClient = oobClients.get(0);
    }


    public OAuthClient getOobClient() {
        return oobClient;
    }


    public void setOobClient(OAuthClient oobClient) {
        this.oobClient = oobClient;
    }
}
