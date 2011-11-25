/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author piotrhol
 *
 */
public class AuthenticatePage
	extends TemplatePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8396464259446612570L;

	private static String authorizationURL;


	public AuthenticatePage()
	{
		super(new PageParameters().add("redirectTo", authorizationURL));
	}


	/**
	 * @return the authorizationURL
	 */
	public static String getAuthorizationURL()
	{
		return authorizationURL;
	}


	/**
	 * @param authorizationURL the authorizationURL to set
	 */
	public static void setAuthorizationURL(String authorizationURL)
	{
		AuthenticatePage.authorizationURL = authorizationURL;
	}
}
