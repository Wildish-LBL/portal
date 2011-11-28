/**
 * 
 */
package pl.psnc.dl.wf4ever.portal;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.scribe.model.Token;

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

	private Token myExpAccessToken;

	private Token requestToken;

	private boolean importDone;

	private String nextUrl;


	public MySession(Request request)
	{
		super(request);
		dLibraAccessToken = tryLoadDlibraTestToken();
		myExpAccessToken = tryLoadMyExpTestToken();
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
	 * @param dLibraAccessToken the dLibraAccessToken to set
	 */
	public void setdLibraAccessToken(Token dLibraAccessToken)
	{
		this.dLibraAccessToken = dLibraAccessToken;
	}


	/**
	 * @return the myExpAccessToken
	 */
	public Token getMyExpAccessToken()
	{
		return myExpAccessToken;
	}


	/**
	 * @param myExpAccessToken the myExpAccessToken to set
	 */
	public void setMyExpAccessToken(Token myExpAccessToken)
	{
		this.myExpAccessToken = myExpAccessToken;
	}


	/**
	 * @return the requestToken
	 */
	public Token getRequestToken()
	{
		return requestToken;
	}


	/**
	 * @param requestToken the requestToken to set
	 */
	public void setRequestToken(Token requestToken)
	{
		this.requestToken = requestToken;
	}


	/**
	 * @return the importDone
	 */
	public boolean isImportDone()
	{
		return importDone;
	}


	/**
	 * @param importDone the importDone to set
	 */
	public void setImportDone(boolean importDone)
	{
		this.importDone = importDone;
	}


	/**
	 * @return the nextUrl
	 */
	public String getNextUrl()
	{
		return nextUrl;
	}


	/**
	 * @param nextUrl the nextUrl to set
	 */
	public void setNextUrl(String nextUrl)
	{
		this.nextUrl = nextUrl;
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


	private Token tryLoadDlibraTestToken()
	{
		Properties props = new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream("testToken.properties"));
			String token = props.getProperty("dLibraToken");
			if (token != null) {
				return new Token(token, null);
			}
		}
		catch (Exception e) {
			log.debug("Failed to load properties: " + e.getMessage());
		}
		return null;
	}


	private Token tryLoadMyExpTestToken()
	{
		Properties props = new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream("testToken.properties"));
			String token = props.getProperty("token");
			String secret = props.getProperty("secret");
			if (token != null && secret != null) {
				return new Token(token, secret);
			}
		}
		catch (Exception e) {
			log.debug("Failed to load properties: " + e.getMessage());
		}
		return null;
	}

}
