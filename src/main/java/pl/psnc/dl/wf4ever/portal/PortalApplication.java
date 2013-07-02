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
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.purl.wf4ever.checklist.client.ChecklistEvaluationService;
import org.purl.wf4ever.rosrs.client.search.OpenSearchSearchServer;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.SolrSearchServer;
import org.purl.wf4ever.rosrs.client.search.SparqlSearchServer;

import pl.psnc.dl.wf4ever.portal.pages.AllRosPage;
import pl.psnc.dl.wf4ever.portal.pages.ContactPage;
import pl.psnc.dl.wf4ever.portal.pages.Error404Page;
import pl.psnc.dl.wf4ever.portal.pages.HelpPage;
import pl.psnc.dl.wf4ever.portal.pages.HomePage;
import pl.psnc.dl.wf4ever.portal.pages.MyExpImportPage;
import pl.psnc.dl.wf4ever.portal.pages.MyRosPage;
import pl.psnc.dl.wf4ever.portal.pages.OAuthPage;
import pl.psnc.dl.wf4ever.portal.pages.SparqlEndpointPage;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.search.SearchResultsPage;
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

    /**
     * Search backend type.
     * 
     * @author piotrekhol
     * 
     */
    public enum SearchType {
        /** OpenSearch, i.e. dLibra. */
        OPENSEARCH,
        /** SPARQL endpoint. */
        SPARQL,
        /** A Solr server. */
        SOLR
    }


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
    private URI searchEndpointURI;

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

    /** checklist service client. */
    private ChecklistEvaluationService checklistService;

    /** RODL admin token. */
    private String adminToken;

    /** Application name, from pom.xml. */
    private String name;

    /** Application version, from pom.xml. */
    private String version;

    /** Type of search server: opensearch / sparql / solr. */
    private SearchType searchType;

    /** Service for performing searches in RODL. */
    private SearchServer searchServer;

    /** Service for generating RSS feeds. */
    private RSSService rssService;


    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }


    @Override
    public void init() {
        super.init();

        getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));
        getMarkupSettings().setDefaultBeforeDisabledLink(null);
        getMarkupSettings().setDefaultAfterDisabledLink(null);
        getRequestCycleSettings().setRenderStrategy(RenderStrategy.REDIRECT_TO_RENDER);

        mountPage("/home", HomePage.class);
        mountPage("/myros", MyRosPage.class);
        mountPage("/allros", AllRosPage.class);
        mountPage("/ro", RoPage.class);
        mountPage("/sparql", SparqlEndpointPage.class);
        mountPage("/myexpimport", MyExpImportPage.class);
        mountPage("/oauth", OAuthPage.class);
        mountPage("/authenticate", AuthenticationPage.class);
        mountPage("/error404", Error404Page.class);
        mountPage("/contact", ContactPage.class);
        mountPage("/help", HelpPage.class);
        mountPage("/profile", ProfilePage.class);
        mountPage("/tokens", AccessTokensPage.class);
        mountPage("/generate", GenerateAccessTokenPage.class);
        mountPage("/authorize", OAuthAuthorizationEndpointPage.class);
        mountPage("/search", SearchResultsPage.class);

        loadProperties("portal.properties");
        loadTokens("tokens.properties");
        loadAdminTokens("admintoken.properties");

        searchServer = createSearchServer();

        rssService = new RSSService(this.getServletContext().getRealPath("/"), sparqlEndpoint, rodlURI);
        rssService.start();

        Locale.setDefault(Locale.ENGLISH);
    }


    /**
     * Initialize a search service client depending on the field {@link SearchType}.
     * 
     * @return opensearch, sparql or Solr endpoint, default is Solr
     */
    private SearchServer createSearchServer() {
        switch (searchType) {
            case OPENSEARCH:
                return new OpenSearchSearchServer(searchEndpointURI);
            case SPARQL:
                return new SparqlSearchServer(sparqlEndpoint);
            case SOLR:
            default:
                return new SolrSearchServer(searchEndpointURI);
        }
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
            searchEndpointURI = new URI(props.getProperty("searchEndpointURL"));
            recommenderEndpointURL = new URL(props.getProperty("recommenderEndpointURL"));
            stabilityEndpointURL = new URL(props.getProperty("stabilityEndpointURL"));
            userAccessTokenEndpointURL = new URL(props.getProperty("userAccessTokenEndpointURL"));
            userAuthorizationEndpointURL = new URL(props.getProperty("userAuthorizationEndpointURL"));
            wf2ROService = new URI(props.getProperty("wf2ROService"));
            name = props.getProperty("application.name");
            version = props.getProperty("application.version");
            URI checklistUri = new URI(props.getProperty("checklist.uri"));
            URI minimUri = new URI(props.getProperty("checklist.minim.uri"));
            try {
                checklistService = new ChecklistEvaluationService(checklistUri, minimUri);
            } catch (Exception e) {
                LOG.error("Failed to initialize the checklist service", e);
            }
            String type = props.getProperty("search.type");
            searchType = SearchType.valueOf(type.trim().toUpperCase());
            if (searchType == null) {
                throw new Exception("Unrecognized search type: " + type);
            }
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


    public String getAppName() {
        return name;
    }


    public String getVersion() {
        return version;
    }


    public ChecklistEvaluationService getChecklistService() {
        return checklistService;
    }


    public SearchServer getSearchServer() {
        return searchServer;
    }

}
