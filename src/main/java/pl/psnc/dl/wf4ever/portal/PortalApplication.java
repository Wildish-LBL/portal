package pl.psnc.dl.wf4ever.portal;

import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import pl.psnc.dl.wf4ever.portal.pages.ContactPage;
import pl.psnc.dl.wf4ever.portal.pages.ErrorPage;
import pl.psnc.dl.wf4ever.portal.pages.HelpPage;
import pl.psnc.dl.wf4ever.portal.pages.MyExpImportPage;
import pl.psnc.dl.wf4ever.portal.pages.OAuthPage;
import pl.psnc.dl.wf4ever.portal.pages.SparqlEndpointPage;
import pl.psnc.dl.wf4ever.portal.pages.all.AllRosPage;
import pl.psnc.dl.wf4ever.portal.pages.home.HomePage;
import pl.psnc.dl.wf4ever.portal.pages.my.MyRosPage;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.users.AccessTokensPage;
import pl.psnc.dl.wf4ever.portal.pages.users.AuthenticationPage;
import pl.psnc.dl.wf4ever.portal.pages.users.GenerateAccessTokenPage;
import pl.psnc.dl.wf4ever.portal.pages.users.OAuthAuthorizationEndpointPage;
import pl.psnc.dl.wf4ever.portal.pages.users.ProfilePage;
import pl.psnc.dl.wf4ever.portal.services.RSSService;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 * 
 * @see pl.psnc.dl.wf4ever.portal.Start#main(String[])
 */
public class PortalApplication extends AuthenticatedWebApplication {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(PortalApplication.class);

    /** dLibra OAuth client ID for the portal application. */
    private String dLibraClientId;

    /** myExperiment OAuth public key for the portal application. */
    private String myExpConsumerKey;

    /** myExperiment OAuth secret key for the portal application. */
    private String myExpConsumerSecret;

    /** Portal OAuth callback URL. */
    private String callbackURL;

    /** RODL URI. */
    private URI rodlURI;

    /** RODL SPARQL URI. */
    private URI sparqlEndpoint;

    /** RODL search service URI. */
    private URL searchEndpointURL;

    /** Recommender service URI. */
    private URL recommenderEndpointURL;

    /** Stability service URI. */
    private URL stabilityEndpointURL;

    /** RODL user management application access token endpoint URI. */
    private URL userAccessTokenEndpointURL;

    /** RODL user management application authorization token endpoint URI. */
    private URL userAuthorizationEndpointURL;

    /** Wf-RO transformation service URI. */
    private URI wf2ROService;

    /** RODL admin token. */
    private String adminToken;


    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }


    @Override
    public void init() {
        super.init();

        getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));

        mountPage("/home", HomePage.class);
        mountPage("/myros", MyRosPage.class);
        mountPage("/allros", AllRosPage.class);
        mountPage("/ro", RoPage.class);
        mountPage("/sparql", SparqlEndpointPage.class);
        mountPage("/myexpimport", MyExpImportPage.class);
        mountPage("/oauth", OAuthPage.class);
        mountPage("/authenticate", AuthenticationPage.class);
        mountPage("/error", ErrorPage.class);
        mountPage("/contact", ContactPage.class);
        mountPage("/help", HelpPage.class);
        mountPage("/profile", ProfilePage.class);
        mountPage("/tokens", AccessTokensPage.class);
        mountPage("/generate", GenerateAccessTokenPage.class);
        mountPage("/authorize", OAuthAuthorizationEndpointPage.class);

        loadProperties("portal.properties");
        loadTokens("tokens.properties");
        loadAdminTokens("admintoken.properties");

        RSSService.start(null, sparqlEndpoint, rodlURI);

        Locale.setDefault(Locale.ENGLISH);
    }


    @Override
    public final Session newSession(Request request, Response response) {
        return new MySession(request);
    }


    /**
     * Load properties file.
     * 
     * @param propertiesFile
     *            filename
     */
    private void loadProperties(String propertiesFile) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
            rodlURI = new URI(props.getProperty("rodlURL"));
            sparqlEndpoint = new URI(props.getProperty("sparqlEndpointURL"));
            searchEndpointURL = new URL(props.getProperty("searchEndpointURL"));
            recommenderEndpointURL = new URL(props.getProperty("recommenderEndpointURL"));
            stabilityEndpointURL = new URL(props.getProperty("stabilityEndpointURL"));
            userAccessTokenEndpointURL = new URL(props.getProperty("userAccessTokenEndpointURL"));
            userAuthorizationEndpointURL = new URL(props.getProperty("userAuthorizationEndpointURL"));
            wf2ROService = new URI(props.getProperty("wf2ROService"));
        } catch (Exception e) {
            LOG.error("Failed to load properties: " + e.getMessage());
        }
    }


    /**
     * Load OAuth access tokens.
     * 
     * @param propertiesFile
     *            filename
     */
    private void loadTokens(String propertiesFile) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
            myExpConsumerKey = props.getProperty("myExpConsumerKey");
            myExpConsumerSecret = props.getProperty("myExpConsumerSecret");
            dLibraClientId = props.getProperty("dLibraClientId");
            callbackURL = props.getProperty("callbackURL");
        } catch (Exception e) {
            LOG.error("Failed to load tokens: " + e.getMessage());
        }
    }


    /**
     * Load RODL admin tokens.
     * 
     * @param propertiesFile
     *            filename
     */
    private void loadAdminTokens(String propertiesFile) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
            adminToken = props.getProperty("adminToken");
        } catch (Exception e) {
            LOG.error("Failed to load admin tokens: " + e.getMessage());
        }
    }


    public String getDLibraClientId() {
        return dLibraClientId;
    }


    public String getMyExpConsumerKey() {
        return myExpConsumerKey;
    }


    public String getMyExpConsumerSecret() {
        return myExpConsumerSecret;
    }


    public String getCallbackURL() {
        return callbackURL;
    }


    public String getAdminToken() {
        return adminToken;
    }


    public URI getRodlURI() {
        return rodlURI;
    }


    public URI getSparqlEndpointURI() {
        return sparqlEndpoint;
    }


    public URL getSearchEndpointURL() {
        return searchEndpointURL;
    }


    public URL getRecommenderEndpointURL() {
        return recommenderEndpointURL;
    }


    public URL getStabilityEndpointURL() {
        return stabilityEndpointURL;
    }


    public URL getUserAccessTokenEndpointURL() {
        return userAccessTokenEndpointURL;
    }


    public void setUserAccessTokenEndpointURL(URL userAccessTokenEndpointURL) {
        this.userAccessTokenEndpointURL = userAccessTokenEndpointURL;
    }


    public URL getUserAuthorizationEndpointURL() {
        return userAuthorizationEndpointURL;
    }


    public void setUserAuthorizationEndpointURL(URL userAuthorizationEndpointURL) {
        this.userAuthorizationEndpointURL = userAuthorizationEndpointURL;
    }


    public URI getWf2ROService() {
        return wf2ROService;
    }


    @Override
    public Class<? extends WebPage> getSignInPageClass() {
        return AuthenticationPage.class;
    }


    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return MySession.class;
    }

}
