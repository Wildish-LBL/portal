/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.services.MyExpApi;

/**
 * @author Piotr Hołubowicz
 *
 */
@AuthorizeInstantiation("USER")
public class MyExpAuthorizePage
	extends TemplatePage
{

	private static final long serialVersionUID = 4637256013660809942L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(MyExpAuthorizePage.class);


	public MyExpAuthorizePage(PageParameters pageParameters)
	{
		super(pageParameters);

		if (MySession.get().getMyExpAccessToken() != null) {
			throw new RestartResponseException(MyExpImportPage.class);
		}

		add(new Link<String>("authorize") {

			private static final long serialVersionUID = 4547048967555063211L;


			@Override
			public void onClick()
			{
				if (MySession.get().getMyExpAccessToken() != null) {
					throw new RestartResponseException(MyExpImportPage.class);
				}
				startMyExpAuthorization();
			}

		});

	}


	/**
	 * 
	 */
	private void startMyExpAuthorization()
	{
		PortalApplication app = (PortalApplication) getApplication();
		OAuthService service = MyExpApi.getOAuthService(app.getMyExpConsumerKey(), app.getMyExpConsumerSecret(),
			app.getCallbackURL());
		Token requestToken = service.getRequestToken();
		MySession.get().setRequestToken(requestToken);
		final String authorizationUrl = service.getAuthorizationUrl(requestToken);
		throw new RestartResponseAtInterceptPageException(ImmediateRedirectPage.class, new PageParameters().add(
			"redirectTo", authorizationUrl));
	}
}
