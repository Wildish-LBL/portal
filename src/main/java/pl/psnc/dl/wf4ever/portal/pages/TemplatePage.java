package pl.psnc.dl.wf4ever.portal.pages;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;

public class TemplatePage
	extends WebPage
{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(TemplatePage.class);

	private static final Class< ? >[] publicPages = { MockupWelcomePage.class, HomePage.class, RoPage.class};


	public TemplatePage(final PageParameters parameters)
	{
		getSession().bind();
		final WebMarkupContainer redirect = new WebMarkupContainer("redirect");
		String redirectionURL = parameters.get("redirectTo").toString();
		if (redirectionURL != null) {
			final String content = "3;URL=" + redirectionURL;
			redirect.add(new AttributeModifier("content", new Model<String>(content)));
		}
		else {
			redirect.setVisible(false);
		}
		add(redirect);

		//		if (MySession.get().getdLibraAccessToken() == null && !ArrayUtils.contains(publicPages, this.getClass())) {
		//			Token at = tryLoadDlibraTestToken();
		//			if (at == null) {
		//				startDlibraAuthorization();
		//				MySession.get().setNextUrl(
		//					RequestCycle.get().getUrlRenderer()
		//							.renderFullUrl(Url.parse(urlFor(this.getClass(), parameters).toString())));
		//			}
		//			else {
		//				MySession.get().setdLibraAccessToken(at);
		//				getSession().debug("Loaded test token for dLibra");
		//			}
		//		}

		add(new BookmarkablePageLink<Void>("menu-home", HomePage.class));
		add(new BookmarkablePageLink<Void>("menu-myros", MyRosPage.class));
	}


	//	private void startDlibraAuthorization()
	//	{
	//		WicketApplication app = (WicketApplication) getApplication();
	//		OAuthService service = DlibraApi.getOAuthService(app.getdLibraClientId(), app.getCallbackURL());
	//		String authorizationUrl = service.getAuthorizationUrl(null);
	//		getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(authorizationUrl));
	//	}
	//
	//
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
