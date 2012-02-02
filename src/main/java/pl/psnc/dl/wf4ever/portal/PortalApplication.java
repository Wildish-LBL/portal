package pl.psnc.dl.wf4ever.portal;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import pl.psnc.dl.wf4ever.portal.pages.AuthenticatePage;
import pl.psnc.dl.wf4ever.portal.pages.ContactPage;
import pl.psnc.dl.wf4ever.portal.pages.ErrorPage;
import pl.psnc.dl.wf4ever.portal.pages.HomePage;
import pl.psnc.dl.wf4ever.portal.pages.MyExpAuthorizePage;
import pl.psnc.dl.wf4ever.portal.pages.MyExpImportPage;
import pl.psnc.dl.wf4ever.portal.pages.MyRosPage;
import pl.psnc.dl.wf4ever.portal.pages.OAuthPage;
import pl.psnc.dl.wf4ever.portal.pages.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.SparqlEndpointPage;
import pl.psnc.dl.wf4ever.portal.services.DlibraApi;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Application object for your web application. If you want to run this application
 * without deploying, run the Start class.
 * 
 * @see pl.psnc.dl.wf4ever.portal.Start#main(String[])
 */
public class PortalApplication
	extends AuthenticatedWebApplication
{

	private static final Logger log = Logger.getLogger(PortalApplication.class);

	private String dLibraClientId;

	private String myExpConsumerKey;

	private String myExpConsumerSecret;

	private String callbackURL;

	private URL sparqlEndpointURL;

	private URL searchEndpointURL;

	private URL recommenderEndpointURL;

	private final Multimap<String, URI> resourceGroups = HashMultimap.create();

	private final Map<String, String> resourceGroupDescriptions = new HashMap<>();


	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class< ? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}


	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));

		mountPage("/wickethome", WicketHomePage.class);
		mountPage("/home", HomePage.class);
		mountPage("/myros", MyRosPage.class);
		mountPage("/ro", RoPage.class);
		mountPage("/sparql", SparqlEndpointPage.class);
		mountPage("/myexpimport", MyExpImportPage.class);
		mountPage("/myexpauthorize", MyExpAuthorizePage.class);
		mountPage("/oauth", OAuthPage.class);
		mountPage("/authenticate", AuthenticatePage.class);
		mountPage("/error", ErrorPage.class);
		mountPage("/contact", ContactPage.class);

		loadTokens("tokens.properties");
		loadProperties("portal.properties");
		loadResourceGroups("resourceGroups.properties");

		Locale.setDefault(Locale.ENGLISH);
	}


	@Override
	public final Session newSession(Request request, Response response)
	{
		return new MySession(request);
	}


	private void loadProperties(String propertiesFile)
	{
		Properties props = new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
			sparqlEndpointURL = new URL(props.getProperty("sparqlEndpointURL"));
			searchEndpointURL = new URL(props.getProperty("searchEndpointURL"));
			recommenderEndpointURL = new URL(props.getProperty("recommenderEndpointURL"));
		}
		catch (Exception e) {
			log.error("Failed to load properties: " + e.getMessage());
		}
	}


	private void loadTokens(String propertiesFile)
	{
		Properties props = new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
			myExpConsumerKey = props.getProperty("myExpConsumerKey");
			myExpConsumerSecret = props.getProperty("myExpConsumerSecret");
			dLibraClientId = props.getProperty("dLibraClientId");
			callbackURL = props.getProperty("callbackURL");

			AuthenticatePage.setAuthorizationURL(DlibraApi.getOAuthService(getdLibraClientId(), getCallbackURL())
					.getAuthorizationUrl(null));
		}
		catch (Exception e) {
			log.error("Failed to load tokens: " + e.getMessage());
		}
	}


	private void loadResourceGroups(String propertiesFile)
	{
		Properties props = new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream(propertiesFile));
			Set<String> groups = props.stringPropertyNames();
			for (String group : groups) {
				if (group.endsWith(".classes")) {
					String[] classes = props.getProperty(group, "").split(",");
					for (String clazz : classes) {
						if (!clazz.trim().isEmpty()) {
							URI classURI = URI.create(clazz.trim());
							if (classURI != null) {
								String key = group.substring(0, group.length() - ".classes".length());
								resourceGroups.put(key, classURI);
							}
						}
					}
					if (!resourceGroupDescriptions.containsKey(group)) {
						resourceGroupDescriptions.put(group, "");
					}
				}
				else if (group.endsWith(".description")) {
					String desc = props.getProperty(group, "");
					String key = group.substring(0, group.length() - ".description".length());
					resourceGroupDescriptions.put(key, desc);
				}
			}
		}
		catch (Exception e) {
			log.error("Failed to load resourceGroups: " + e.getMessage());
		}
	}


	/**
	 * @return the dLibraClientId
	 */
	public String getdLibraClientId()
	{
		return dLibraClientId;
	}


	/**
	 * @return the myExpConsumerKey
	 */
	public String getMyExpConsumerKey()
	{
		return myExpConsumerKey;
	}


	/**
	 * @return the myExpConsumerSecret
	 */
	public String getMyExpConsumerSecret()
	{
		return myExpConsumerSecret;
	}


	/**
	 * @return the callbackURL
	 */
	public String getCallbackURL()
	{
		return callbackURL;
	}


	@Override
	protected Class< ? extends WebPage> getSignInPageClass()
	{
		return AuthenticatePage.class;
	}


	@Override
	protected Class< ? extends AbstractAuthenticatedWebSession> getWebSessionClass()
	{
		return MySession.class;
	}


	/**
	 * @return the sparqlEndpoint
	 */
	public URL getSparqlEndpointURL()
	{
		return sparqlEndpointURL;
	}


	/**
	 * @param sparqlEndpoint
	 *            the sparqlEndpoint to set
	 */
	public void setSparqlEndpointURL(URL sparqlEndpoint)
	{
		this.sparqlEndpointURL = sparqlEndpoint;
	}


	/**
	 * @return the searchEndpointURL
	 */
	public URL getSearchEndpointURL()
	{
		return searchEndpointURL;
	}


	/**
	 * @param searchEndpointURL
	 *            the searchEndpointURL to set
	 */
	public void setSearchEndpointURL(URL searchEndpointURL)
	{
		this.searchEndpointURL = searchEndpointURL;
	}


	/**
	 * @return the recommenderEndpointURL
	 */
	public URL getRecommenderEndpointURL()
	{
		return recommenderEndpointURL;
	}


	/**
	 * @param recommenderEndpointURL
	 *            the recommenderEndpointURL to set
	 */
	public void setRecommenderEndpointURL(URL recommenderEndpointURL)
	{
		this.recommenderEndpointURL = recommenderEndpointURL;
	}


	/**
	 * @return the resourceGroups
	 */
	public Multimap<String, URI> getResourceGroups()
	{
		return resourceGroups;
	}


	/**
	 * @return the resourceGroupDescriptions
	 */
	public Map<String, String> getResourceGroupDescriptions()
	{
		return resourceGroupDescriptions;
	}

}
