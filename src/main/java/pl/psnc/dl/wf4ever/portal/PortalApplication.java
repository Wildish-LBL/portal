package pl.psnc.dl.wf4ever.portal;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.IRequestCycleSettings.RenderStrategy;
import org.purl.wf4ever.checklist.client.ChecklistEvaluationService;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;
import org.purl.wf4ever.rosrs.client.search.OpenSearchSearchServer;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.SolrSearchServer;
import org.purl.wf4ever.rosrs.client.search.SparqlSearchServer;

import pl.psnc.dl.wf4ever.portal.components.form.AbsoluteURIConverter;
import pl.psnc.dl.wf4ever.portal.model.MinimModel;
import pl.psnc.dl.wf4ever.portal.pages.AboutPage;
import pl.psnc.dl.wf4ever.portal.pages.CreateROFromZipPage;
import pl.psnc.dl.wf4ever.portal.pages.Error404Page;
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
import pl.psnc.dl.wf4ever.portal.services.RODLUtilities;
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

    /** List of all minim models and purposes. */
    private List<MinimModel> minimModels;

    /** The minim model to use in default RO evaluation. */
    private MinimModel defaultMinimModel;

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

    /** Selected featured ROs. */
    private List<ResearchObject> featuredROs;

    /** Most recent ROs,cache'd. */
    private Map<URI, ResearchObject> recentROs = Collections.emptyMap();


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
        mountPage("/ro", RoPage.class);
        mountPage("/sparql", SparqlEndpointPage.class);
        mountPage("/myexpimport", MyExpImportPage.class);
        mountPage("/oauth", OAuthPage.class);
        mountPage("/authenticate", AuthenticationPage.class);
        mountPage("/error404", Error404Page.class);
        mountPage("/about", AboutPage.class);
        mountPage("/profile", ProfilePage.class);
        mountPage("/tokens", AccessTokensPage.class);
        mountPage("/generate", GenerateAccessTokenPage.class);
        mountPage("/authorize", OAuthAuthorizationEndpointPage.class);
        mountPage("/search", SearchResultsPage.class);
        mountPage("/zip", CreateROFromZipPage.class);

        loadProperties("portal.properties");
        loadChecklistProperties("checklist.properties");
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
        try {
            PropertiesConfiguration props = new PropertiesConfiguration(propertiesFile);
            rodlURI = new URI(props.getString("rodlURL"));
            sparqlEndpoint = new URI(props.getString("sparqlEndpointURL"));
            searchEndpointURI = new URI(props.getString("searchEndpointURL"));
            recommenderEndpointURL = new URL(props.getString("recommenderEndpointURL"));
            stabilityEndpointURL = new URL(props.getString("stabilityEndpointURL"));
            userAccessTokenEndpointURL = new URL(props.getString("userAccessTokenEndpointURL"));
            userAuthorizationEndpointURL = new URL(props.getString("userAuthorizationEndpointURL"));
            wf2ROService = new URI(props.getString("wf2ROService"));
            name = props.getString("application.name");
            version = props.getString("application.version");
            String type = props.getString("search.type");
            searchType = SearchType.valueOf(type.trim().toUpperCase());
            if (searchType == null) {
                throw new Exception("Unrecognized search type: " + type);
            }
            featuredROs = new ArrayList<>();
            //FIXME ROs/ should not be hardcoded
            ROSRService rosrs = new ROSRService(rodlURI.resolve("ROs/"), null);
            for (Object f : props.getList("ros.featured")) {
                try {
                    URI uri = new URI(f.toString().trim());
                    ResearchObject ro = new ResearchObject(uri, rosrs);
                    LOG.debug("Loading featured RO manifest: " + ro.getUri());
                    ro.loadManifest();
                    LOG.debug("Loaded");
                    featuredROs.add(ro);
                } catch (Exception e) {
                    LOG.error("Can't load RO: " + f, e);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to load properties: " + e.getMessage());
        }
    }


    /**
     * Load checklist properties file.
     * 
     * @param propertiesFile
     *            filename
     */
    private void loadChecklistProperties(String propertiesFile) {
        try {
            PropertiesConfiguration props = new PropertiesConfiguration(propertiesFile);
            URI checklistUri = new URI(props.getString("service.uri"));
            try {
                checklistService = new ChecklistEvaluationService(checklistUri);
            } catch (Exception e) {
                LOG.error("Failed to initialize the checklist service", e);
            }
            String defaultMinim = props.getString("minims.default");
            minimModels = new ArrayList<>();
            for (Object m : props.getList("minims")) {
                try {
                    URI uri = new URI(props.getString("minim." + m + ".uri"));
                    String purpose = props.getString("minim." + m + ".purpose");
                    String title = props.getString("minim." + m + ".title");
                    String description = props.getString("minim." + m + ".description");
                    MinimModel minim = new MinimModel(uri, purpose, title, description);
                    minimModels.add(minim);
                    if (m.equals(defaultMinim)) {
                        defaultMinimModel = minim;
                    }
                } catch (Exception e) {
                    LOG.error("Failed to load checklist minim model " + m + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to load checklist properties: " + e.getMessage());
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


    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = (ConverterLocator) super.newConverterLocator();
        locator.set(URI.class, new AbsoluteURIConverter());
        return locator;
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


    public List<ResearchObject> getFeaturedROs() {
        return featuredROs;
    }


    /**
     * Return the most recent ROs, loading them from cache if possible.
     * 
     * @param cnt
     *            the number of most recent ROs to return
     * @return a list of ROs, starting with the most recently created
     */
    public List<ResearchObject> getRecentROs(int cnt) {
        //FIXME ROs/ should not be hardcoded
        ROSRService rosrs = new ROSRService(rodlURI.resolve("ROs/"), null);
        try {
            List<URI> uris = RODLUtilities.getMostRecentROs(sparqlEndpoint, cnt);
            Map<URI, ResearchObject> newRecentROs = new LinkedHashMap<>();
            for (URI uri : uris) {
                if (this.recentROs.containsKey(uri)) {
                    newRecentROs.put(uri, this.recentROs.get(uri));
                } else {
                    ResearchObject ro = new ResearchObject(uri, rosrs);
                    try {
                        ro.loadManifest();
                        newRecentROs.put(uri, ro);
                    } catch (ROSRSException | ROException e) {
                        LOG.error("Can't load the RO manifest for " + ro.getUri(), e);
                    }
                }
            }
            this.recentROs = newRecentROs;
        } catch (IOException e1) {
            LOG.error("Can't load recent ROs from the SPARQL endpoint", e1);
        }
        return new ArrayList<>(recentROs.values());
    }


    public List<MinimModel> getMinimModels() {
        return minimModels;
    }


    public MinimModel getDefaultMinimModel() {
        return defaultMinimModel;
    }

}
