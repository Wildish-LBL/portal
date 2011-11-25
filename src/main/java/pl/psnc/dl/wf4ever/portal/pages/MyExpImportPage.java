/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.request.http.handler.RedirectRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.myexpimport.model.ImportModel;
import pl.psnc.dl.wf4ever.myexpimport.model.myexp.User;
import pl.psnc.dl.wf4ever.myexpimport.wizard.ImportWizard;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.services.MyExpApi;
import pl.psnc.dl.wf4ever.portal.services.MyExpImportService;
import pl.psnc.dl.wf4ever.portal.utils.MySession;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpImportPage
	extends TemplatePage
{

	private static final long serialVersionUID = 4637256013660809942L;


	public MyExpImportPage(PageParameters pageParameters)
	{
		super(pageParameters);

		PortalApplication app = (PortalApplication) getApplication();
		OAuthService service = MyExpApi.getOAuthService(app.getMyExpConsumerKey(), app.getMyExpConsumerSecret(),
			app.getCallbackURL());

		try {
			User myExpUser = MyExpImportService.retrieveMyExpUser(MySession.get().getMyExpAccessToken(), service);
			ImportModel model = new ImportModel(myExpUser);
			add(new ImportWizard("wizard", model));
		}
		catch (Exception e) {
			String page = urlFor(ErrorPage.class, null).toString() + "?message=" + e.getMessage();
			getRequestCycle().scheduleRequestHandlerAfterCurrent(new RedirectRequestHandler(page));
			return;
		}
	}

}
