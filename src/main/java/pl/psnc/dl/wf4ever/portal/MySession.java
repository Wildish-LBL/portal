/**
 * 
 */
package pl.psnc.dl.wf4ever.portal;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.cookies.CookieUtils;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * @author piotrhol
 * 
 */
public class MySession
	extends AbstractAuthenticatedWebSession
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4113134277706549806L;

	private static final Logger log = Logger.getLogger(MySession.class);

	private Token dLibraAccessToken;

	private boolean dirtydLibra = false;

	private Token myExpAccessToken;

	private boolean dirtyMyExp = false;

	private Token requestToken;

	private URI userURI;

	private String username;

	private final static String DLIBRA_KEY = "dlibra";

	private final static String MYEXP_KEY_TOKEN = "myexp1";

	private final static String MYEXP_KEY_SECRET = "myexp2";

	private final Map<URI, String> usernames = new HashMap<URI, String>();


	public MySession(Request request)
	{
		super(request);
		if (new CookieUtils().load(DLIBRA_KEY) != null)
			setdLibraAccessToken(new Token(new CookieUtils().load(DLIBRA_KEY), null));
		if (new CookieUtils().load(MYEXP_KEY_TOKEN) != null && new CookieUtils().load(MYEXP_KEY_SECRET) != null) {
			myExpAccessToken = new Token(new CookieUtils().load(MYEXP_KEY_TOKEN),
					new CookieUtils().load(MYEXP_KEY_SECRET));
		}
	}


	public static MySession get()
	{
		return (MySession) Session.get();
	}


	/**
	 * @return the dLibraAccessToken
	 */
	public Token getdLibraAccessToken()
	{
		return dLibraAccessToken;
	}


	/**
	 * @param dLibraAccessToken
	 *            the dLibraAccessToken to set
	 */
	public void setdLibraAccessToken(Token dLibraAccessToken)
	{
		this.dLibraAccessToken = dLibraAccessToken;
		fetchUserData();
		dirtydLibra = true;
	}


	/**
	 * @return the myExpAccessToken
	 */
	public Token getMyExpAccessToken()
	{
		return myExpAccessToken;
	}


	/**
	 * @param myExpAccessToken
	 *            the myExpAccessToken to set
	 */
	public void setMyExpAccessToken(Token myExpAccessToken)
	{
		this.myExpAccessToken = myExpAccessToken;
		dirtyMyExp = true;
	}


	/**
	 * @return the requestToken
	 */
	public Token getRequestToken()
	{
		return requestToken;
	}


	/**
	 * @param requestToken
	 *            the requestToken to set
	 */
	public void setRequestToken(Token requestToken)
	{
		this.requestToken = requestToken;
	}


	@Override
	public Roles getRoles()
	{
		return isSignedIn() ? new Roles(Roles.USER) : null;
	}


	@Override
	public boolean isSignedIn()
	{
		return getdLibraAccessToken() != null;
	}


	public void signOut()
	{
		dLibraAccessToken = null;
		myExpAccessToken = null;
		username = null;
		new CookieUtils().remove(DLIBRA_KEY);
		new CookieUtils().remove(MYEXP_KEY_TOKEN);
		new CookieUtils().remove(MYEXP_KEY_SECRET);
	}


	public void persist()
	{
		if (dirtydLibra) {
			if (dLibraAccessToken != null) {
				new CookieUtils().save(DLIBRA_KEY, dLibraAccessToken.getToken());
			}
			dirtydLibra = false;
		}
		if (dirtyMyExp) {
			if (myExpAccessToken != null) {
				new CookieUtils().save(MYEXP_KEY_TOKEN, myExpAccessToken.getToken());
				new CookieUtils().save(MYEXP_KEY_SECRET, myExpAccessToken.getSecret());
			}
			dirtyMyExp = false;
		}
	}


	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}


	/**
	 * @return the userURI
	 */
	public URI getUserURI()
	{
		return userURI;
	}


	/**
	 * @return the username
	 */
	public String getUsername(String defaultValue)
	{
		if (username != null)
			return username;
		return defaultValue;
	}


	private void fetchUserData()
	{
		try {
			OntModel userModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
			userModel.read(ROSRService.getWhoAmi(getdLibraAccessToken()), null);
			ExtendedIterator<Individual> it = userModel.listIndividuals(RoFactory.foafAgent);
			Individual user = it.next();
			if (user != null && user.hasProperty(RoFactory.foafName)) {
				userURI = new URI(user.getURI());
				username = user.as(Individual.class).getPropertyValue(RoFactory.foafName).asLiteral().getString();
			}
		}
		catch (Exception e) {
			log.error("Error when retrieving user data: " + e.getMessage());
		}
	}


	/**
	 * @return the usernames
	 */
	public Map<URI, String> getUsernames()
	{
		return usernames;
	}

}
