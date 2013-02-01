package pl.psnc.dl.wf4ever.portal.pages.users;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.users.OpenIdUser;
import pl.psnc.dl.wf4ever.portal.services.OpenIdService;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * This page receives responses from OpenID Providers.
 * 
 * @author piotrekhol
 * 
 */
public class OpenIDEndpoint extends WebPage {

    /** id. */
    private static final long serialVersionUID = 6262904394440709890L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(OpenIDEndpoint.class);


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters provided by the OP
     */
    public OpenIDEndpoint(PageParameters pageParameters) {
        super(pageParameters);
        String openIdMode = pageParameters.get("openid.mode").toString();
        MySession session = (MySession) getSession();
        if ("cancel".equals(openIdMode)) {
            session.info("The authentication request has been rejected");
        } else {
            OpenIdUser openIdUser = OpenIdService.processReturn(session.getDiscoveryInformation(), pageParameters,
                session.getOpenIDCallbackURI());
            if (openIdUser == null) {
                session.error("Open ID Confirmation Failed. No information was retrieved from the OpenID Provider.");
            } else {
                register(openIdUser);
                if (!continueToOriginalDestination()) {
                    LOG.warn("Could not find the original destination");
                    throw new RestartResponseException(getApplication().getHomePage());
                }
            }
        }
        throw new RestartResponseException(AuthenticationPage.class);
    }


    /**
     * Register the user in RODL.
     * 
     * @param user
     *            openID attributes
     * @return true if a new account has been created, false otherwise
     */
    private boolean register(OpenIdUser user) {
        PortalApplication app = ((PortalApplication) getApplication());
        MySession session = (MySession) getSession();
        boolean newAccount = false;
        if (!session.getUms().userExistsInDlibra(user.getOpenId())) {
            try {
                ClientResponse response = session.getUms().createUser(user.getOpenId(), user.getFullName());
                if (response.getStatus() == HttpServletResponse.SC_CREATED) {
                    newAccount = true;
                    getSession().info("New account has been created.");
                } else if (response.getStatus() == HttpServletResponse.SC_CONFLICT) {
                    getSession().info(
                        "An account for this username already existed "
                                + "in dLibra, you have been registered with it.");
                } else {
                    getSession().error(response.getClientResponseStatus());
                }
                response.close();
            } catch (Exception e) {
                getSession().error(e.getMessage() != null ? e.getMessage() : "Unknown error");
            }
        }
        try {
            String token = session.getUms().createAccessToken(user.getOpenId(), app.getDLibraClientId());
            session.signIn(token);
        } catch (UniformInterfaceException e) {
            String error = e.getResponse().getClientResponseStatus() + " " + e.getResponse().getEntity(String.class);
            getSession().error(error);
            LOG.error("Error when creating access token: " + error);
        }
        return newAccount;
    }
}
