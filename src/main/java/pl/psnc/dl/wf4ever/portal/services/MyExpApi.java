/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.net.URI;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

/**
 * myExperiment OAuth API.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class MyExpApi extends DefaultApi10a {

    /** OAuth verifier. */
    public static final String OAUTH_VERIFIER = "oauth_token";

    /** myExperiment who-am-I service URL. */
    public static final URI WHOAMI_URL = URI.create("http://www.myexperiment.org/whoami.xml");

    /** Get user myExperiment URL. */
    public static final String GET_USER_URL_TMPL = "http://www.myexperiment.org/user.xml?id=%d&elements=id,openid-url,name,email,city,country,website,packs,workflows,files";


    /**
     * Factory method.
     * 
     * @param consumerKey
     *            consumer key
     * @param consumerSecret
     *            consumer secret
     * @param oauthCallbackURL
     *            OAuth callback URL
     * @return OAuth service
     */
    public static OAuthService getOAuthService(String consumerKey, String consumerSecret, String oauthCallbackURL) {
        return new ServiceBuilder().provider(MyExpApi.class).apiKey(consumerKey).apiSecret(consumerSecret)
                .callback(oauthCallbackURL).build();
    }


    /**
     * Factory method.
     * 
     * @param consumerKey
     *            consumer key
     * @param consumerSecret
     *            consumer secret
     * @return OAuth service
     */
    public static OAuthService getOAuthService(String consumerKey, String consumerSecret) {
        return new ServiceBuilder().provider(MyExpApi.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
    }


    /* (non-Javadoc)
     * @see org.scribe.builder.api.DefaultApi10a#getAccessTokenEndpoint()
     */
    @Override
    public String getAccessTokenEndpoint() {
        return "http://www.myexperiment.org/oauth/access_token";
    }


    /* (non-Javadoc)
     * @see org.scribe.builder.api.DefaultApi10a#getAuthorizationUrl(org.scribe.model.Token)
     */
    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return "http://www.myexperiment.org/oauth/authorize?oauth_token=" + requestToken.getToken();
    }


    /* (non-Javadoc)
     * @see org.scribe.builder.api.DefaultApi10a#getRequestTokenEndpoint()
     */
    @Override
    public String getRequestTokenEndpoint() {
        return "http://www.myexperiment.org/oauth/request_token";
    }

}
