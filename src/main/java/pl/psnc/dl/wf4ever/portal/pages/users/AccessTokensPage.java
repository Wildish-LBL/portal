package pl.psnc.dl.wf4ever.portal.pages.users;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.OddEvenListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.common.users.AccessToken;
import org.purl.wf4ever.rosrs.client.common.users.OAuthClient;
import org.purl.wf4ever.rosrs.client.common.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.User;
import pl.psnc.dl.wf4ever.portal.pages.TemplatePage;

/**
 * This page presents a list of access tokens of the current user.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@AuthorizeInstantiation("USER")
public class AccessTokensPage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AccessTokensPage.class);

    /** The selected out of band client. */
    private OAuthClient oobClient;

    /** List view of all user's access tokens. */
    private ListView<AccessToken> list;

    /** List of all user's access tokens. */
    private List<AccessToken> accessTokens = null;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters
     */
    @SuppressWarnings("serial")
    public AccessTokensPage(PageParameters pageParameters) {
        super(pageParameters);

        final User user = ((MySession) getSession()).getUser();

        PortalApplication app = ((PortalApplication) getApplication());
        accessTokens = UserManagementService.getAccessTokens(app.getRodlURI(), app.getAdminToken(), user.getURI()
                .toString());

        final Form<?> form = new Form<Void>("form");
        add(form);
        list = new ListView<AccessToken>("tokensListView", new PropertyModel<List<AccessToken>>(this, "accessTokens")) {

            @Override
            protected ListItem<AccessToken> newItem(int index, IModel<AccessToken> itemModel) {
                return new OddEvenListItem<AccessToken>(index, itemModel);
            };


            protected void populateItem(ListItem<AccessToken> item) {
                final AccessToken token = (AccessToken) item.getModelObject();
                item.add(new Label("clientName", token.getClient().getName()));
                item.add(new Label("created", token.getCreated() != null ? token.getCreated().toString() : "--"));
                item.add(new Label("lastUsed", token.getLastUsed() != null ? token.getLastUsed().toString() : "--"));
                item.add(new AjaxButton("revoke", form) {

                    @Override
                    protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
                        // TODO Auto-generated method stub

                    }


                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
                        PortalApplication app = ((PortalApplication) getApplication());
                        try {
                            UserManagementService.deleteAccessToken(app.getRodlURI(), app.getAdminToken(),
                                token.getToken());
                            accessTokens.remove(token);
                            target.add(form);
                        } catch (Exception e) {
                            LOG.error(e.getMessage());
                            error(e.getMessage());
                        }
                    }
                });
            }
        };
        list.setReuseItems(true);
        list.setOutputMarkupId(true);
        form.add(list);

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


    public List<AccessToken> getAccessTokens() {
        return accessTokens;
    }


    public void setAccessTokens(List<AccessToken> accessTokens) {
        this.accessTokens = accessTokens;
    }
}
