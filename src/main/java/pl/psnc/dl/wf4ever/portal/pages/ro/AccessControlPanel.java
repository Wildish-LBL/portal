package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.accesscontrol.AccessControlService;
import org.purl.wf4ever.rosrs.client.users.User;
import org.purl.wf4ever.rosrs.client.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.annotations.AddAnnotationClickedEvent;

public class AccessControlPanel extends Panel {

        /** id. */
        private static final long serialVersionUID = -3775797988389365540L;

        /** Logger. */
        @SuppressWarnings("unused")
        private static final Logger LOG = Logger.getLogger(AccessControlPanel.class);

        private IModel<ResearchObject> roModel;
        PortalApplication app;
        final User user;
        final UserManagementService ums;
        final MySession session;
        final AccessControlService accessControlService;
        public AccessControlPanel(String id, IModel<ResearchObject> model) {
                super(id);
                this.roModel = model;
                app = (PortalApplication) getApplication();
                session = (MySession) getSession();
                user = session.getUser();
                ums = session.getUms();
                accessControlService = session.getAccessControlService();
        		setOutputMarkupPlaceholderTag(true);
        		Form<Void> form = new Form<Void>("form");
        		add(form);
        		form.add(new AnnotationEditAjaxEventButton("grant-priviliage", form, model, this,
        				AddAnnotationClickedEvent.class));
        }
        
        private void printPermissions() {
        	//List<Permission> permission = accessControlService.getPermissions(roModel.getObject().getUri());
        }

}