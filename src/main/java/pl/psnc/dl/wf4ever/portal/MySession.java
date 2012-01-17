/**
 * 
 */
package pl.psnc.dl.wf4ever.portal;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.cookies.CookieUtils;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.portal.services.ROSRService;

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

	private String username;

	private final static String DLIBRA_KEY = "dlibra";

	private final static String MYEXP_KEY_TOKEN = "myexp1";

	private final static String MYEXP_KEY_SECRET = "myexp2";


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
		this.username = fetchUsername();
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
	 * @return the username
	 */
	public String getUsername(String defaultValue)
	{
		if (username != null)
			return username;
		return defaultValue;
	}


	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}


	private String fetchUsername()
	{
		try {
			String[] data = ROSRService.getWhoAmi(getdLibraAccessToken());
			if (data.length >= 2)
				return data[1];
			if (data.length >= 1)
				return data[0];
			return null;
		}
		catch (Exception e) {
			log.error("Error when retrieving username: " + e.getMessage());
			return null;
		}
	}

}
