/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.users;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.common.users.OAuthClient;
import org.purl.wf4ever.rosrs.client.common.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.User;
import pl.psnc.dl.wf4ever.portal.model.users.AuthCodeData;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
import pl.psnc.dl.wf4ever.portal.services.HibernateService;

import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * This is the OAuth 2.0 authorization endpoint. On this page the user accepts to give authorization.
 * 
 * @author Piotr Hołubowicz
 */
public class OAuthAuthorizationEndpointPage extends Base {

    /** id. */
    private static final long serialVersionUID = 3793214124123802219L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(OAuthAuthorizationEndpointPage.class);

    /** OAuth client. */
    private OAuthClient client;

    /** OAuth state returned. */
    private String state;

    /** Callback URL provided by the client. */
    private String providedRedirectURI;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters
     */
    public OAuthAuthorizationEndpointPage(PageParameters pageParameters) {
        super(pageParameters);
        if (pageParameters.get("response_type").isNull()) {
            error("Missing response type.");
        } else {
            String responseType = pageParameters.get("response_type").toString();
            if (responseType.equals("token") || responseType.equals("code")) {
                this.client = processImplicitGrantOrAuthCodeFlow(pageParameters);
                if (client != null && !OAuthClient.OOB.equals(client.getRedirectionURI())) {
                    add(new AuthorizeFragment("entry", "validRequest", this, client, responseType));
                } else {
                    add(new Fragment("entry", "invalidRequest", this));
                }
            } else {
                error(String.format("Unknown response type: %s.", responseType));
                add(new Fragment("entry", "invalidRequest", this));
            }
        }
    }


    /**
     * Find OAuth client based on request params.
     * 
     * @param pageParameters
     *            request params
     * @return OAuth client or null
     */
    private OAuthClient processImplicitGrantOrAuthCodeFlow(PageParameters pageParameters) {
        if (pageParameters.get("client_id").isNull()) {
            error("Missing client id.");
        } else {
            String clientId = pageParameters.get("client_id").toString();
            try {
                PortalApplication app = ((PortalApplication) getApplication());
                OAuthClient tentativeClient = UserManagementService.getClient(app.getRodlURI(), app.getAdminToken(),
                    clientId);
                if (pageParameters.get("redirect_uri").isNull()) {
                    LOG.warn("Missing redirect URI.");
                } else {
                    providedRedirectURI = pageParameters.get("redirect_uri").toString();
                    if (!tentativeClient.getRedirectionURI().equals(providedRedirectURI)) {
                        error("Redirect URI does not match client redirect URI.");
                        return null;
                    }
                }
                if (!pageParameters.get("state").isNull()) {
                    state = pageParameters.get("state").toString();
                }
                return tentativeClient;
            } catch (Exception e) {
                error("Invalid client id: " + e.getMessage() + ".");
            }
        }
        return null;
    }


    /**
     * Prepare the response URL when token was requested.
     * 
     * @param client
     *            OAuth client
     * @return URL as a String with data as query params
     * @throws UniformInterfaceException
     *             wrong response status
     */
    private String prepareTokenResponse(OAuthClient client)
            throws UniformInterfaceException {
        User user = ((MySession) getSession()).getUser();
        PortalApplication app = ((PortalApplication) getApplication());
        String token = UserManagementService.createAccessToken(app.getRodlURI(), app.getAdminToken(), user.getURI()
                .toString(), client.getClientId());
        String url = client.getRedirectionURI() + "#";
        url += ("access_token=" + token);
        url += "&token_type=bearer";
        if (state != null) {
            url += ("&state=" + state);
        }
        return url;
    }


    /**
     * Prepare the response URL when authorization code was requested.
     * 
     * @param client
     *            OAuth client
     * @return URL as a String with data as query params
     */
    private String prepareAuthCodeResponse(OAuthClient client) {
        User user = ((MySession) getSession()).getUser();
        String code = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);

        AuthCodeData data = new AuthCodeData(code, providedRedirectURI, user.getURI().toString(), client.getClientId());
        HibernateService.storeCode(data);
        String url = client.getRedirectionURI() + "?";
        url += ("code=" + code);
        if (state != null) {
            url += ("&state=" + state);
        }
        return url;
    }


    /**
     * Prepare the response URL when the user did not give authorization.
     * 
     * @param client
     *            OAuth client
     * @return URL as a String with data as query params
     */
    private String prepareDeniedResponse(OAuthClient client) {
        String url = client.getRedirectionURI() + "#";
        url += "error=access_denied";
        if (state != null) {
            url += ("&state=" + state);
        }
        return url;
    }


    /**
     * The fragment that displays the authorization prompt.
     * 
     * @author piotrekhol
     * 
     */
    private class AuthorizeFragment extends Fragment {

        /** id. */
        private static final long serialVersionUID = -3040124186474465047L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket destination id
         * @param markupId
         *            fragment wicket id
         * @param markupProvider
         *            fragment container
         * @param client
         *            OAuth client
         * @param responseType
         *            OAuth response type
         */
        @SuppressWarnings("serial")
        public AuthorizeFragment(String id, String markupId, MarkupContainer markupProvider, final OAuthClient client,
                final String responseType) {
            super(id, markupId, markupProvider);
            Form<?> form = new Form<Void>("form");
            add(form);
            form.add(new Label("name", client.getName()));
            form.add(new Button("authorize") {

                @Override
                public void onSubmit() {
                    super.onSubmit();
                    try {
                        String url;
                        if (responseType.equals("token")) {
                            url = prepareTokenResponse(client);
                        } else {
                            url = prepareAuthCodeResponse(client);
                        }
                        getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(url));
                    } catch (Exception e) {
                        error(e);
                        LOG.error(e);
                    }
                }

            });
            form.add(new Button("reject") {

                @Override
                public void onSubmit() {
                    super.onSubmit();
                    String url = prepareDeniedResponse(client);
                    getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(url));
                }
            });

        }
    }
}
