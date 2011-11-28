/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
@AuthorizeInstantiation("USER")
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
			throw new RestartResponseException(ErrorPage.class, new PageParameters().add("message", e.getMessage()));
		}
	}

}
