package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.notifications.NotificationService;
import org.purl.wf4ever.rosrs.client.users.AccessToken;
import org.purl.wf4ever.rosrs.client.users.User;
import org.purl.wf4ever.rosrs.client.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
public class AccessControlPanel extends Panel {

	/** id. */
	private static final long serialVersionUID = -3775797988389365540L;

	/** Logger. */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AccessControlPanel.class);

	private IModel<ResearchObject> roModel;
	PortalApplication app;

	public AccessControlPanel(String id, IModel<ResearchObject> model) {
		super(id);
		this.roModel = model;
		app = (PortalApplication) getApplication();
		NotificationService notificationService = new NotificationService(app.getRodlURI(), null);
		MySession session = (MySession) getSession();
		final User user = session.getUser();
		final UserManagementService ums = session.getUms();
		List<AccessToken> accessTokens = ums.getAccessTokens(user.getURI().toString());
		int i = accessTokens.size();
	}

}
