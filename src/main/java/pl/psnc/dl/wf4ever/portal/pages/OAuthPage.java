package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.services.MyExpApi;

/**
 * This page receives responses from OAuth servers and redirects to the page that originated the request or to the home
 * page.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class OAuthPage extends WebPage {

    /** id. */
    private static final long serialVersionUID = -3233388849667095897L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(OAuthPage.class);


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            contain response details from the OAuth server
     * @throws URISyntaxException
     *             the response details contain invalid URIs
     */
    public OAuthPage(PageParameters pageParameters)
            throws URISyntaxException {
        super(pageParameters);

        MySession session = MySession.get();
        PortalApplication app = (PortalApplication) getApplication();

        if (session.getMyExpAccessToken() == null) {
            OAuthService service = MyExpApi.getOAuthService(app.getMyExpConsumerKey(), app.getMyExpConsumerSecret(),
                app.getCallbackURL());
            Token token = retrieveMyExpAccessToken(pageParameters, service);
            session.setMyExpAccessToken(token);
            if (token != null) {
                LOG.info("Successfully received myExperiment access token");
            }
        }
        if (!continueToOriginalDestination()) {
            LOG.warn("Could not find the original destination");
            throw new RestartResponseException(getApplication().getHomePage());
        }
    }


    /**
     * Retrieve an OAuth 1.0 access token from the myExperiment response.
     * 
     * @param pageParameters
     *            page params
     * @param service
     *            myExperiment OAuth service instance
     * @return the access token
     */
    private Token retrieveMyExpAccessToken(PageParameters pageParameters, OAuthService service) {
        Token accessToken = null;
        if (!pageParameters.get(MyExpApi.OAUTH_VERIFIER).isEmpty()) {
            Verifier verifier = new Verifier(pageParameters.get(MyExpApi.OAUTH_VERIFIER).toString());
            Token requestToken = MySession.get().getRequestToken();
            LOG.debug("Request token: " + requestToken.toString() + " verifier: " + verifier.getValue() + " service: "
                    + service.getAuthorizationUrl(requestToken));
            accessToken = service.getAccessToken(requestToken, verifier);
        }
        return accessToken;
    }

}
