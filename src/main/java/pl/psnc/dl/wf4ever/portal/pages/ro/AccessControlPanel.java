package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.accesscontrol.AccessControlService;
import org.purl.wf4ever.rosrs.client.accesscontrol.AccessMode;
import org.purl.wf4ever.rosrs.client.accesscontrol.Mode;
import org.purl.wf4ever.rosrs.client.accesscontrol.Permission;
import org.purl.wf4ever.rosrs.client.accesscontrol.Role;
import org.purl.wf4ever.rosrs.client.users.User;
import org.purl.wf4ever.rosrs.client.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.ProtectedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.permissions.GrantPermissionClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.permissions.PermissionApplyClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.permissions.PermissionCancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.permissions.PermissionDeletedClickedEvent;

import com.sun.jersey.api.client.ClientResponse;

public class AccessControlPanel extends Panel {

        private static final long serialVersionUID = -3775797988389365540L;

        @SuppressWarnings("unused")
        private static final Logger LOG = Logger.getLogger(AccessControlPanel.class);

        private IModel<ResearchObject> roModel;
        PortalApplication app;
        final User user;
        final UserManagementService ums;
        final MySession session;
        final AccessControlService accessControlService;
        List<Permission> permissions;
        List<Permission> editors;
        List<Permission> readers;
        
        Permission toBeDeleted;
        ListView<Permission> editorsListView;
        ListView<Permission> readersListView;
        private Role selected;
        WebMarkupContainer formContainer;
        RequiredTextField<URI> useruri;
        boolean isPrivate;
        DropDownChoice<Role> choices;
        List<Role> roles;
        /** Feedback panel. */
        private MyFeedbackPanel feedbackPanel;

        public AccessControlPanel(String id, IModel<ResearchObject> model) {
                super(id);
                this.roModel = model;
                app = (PortalApplication) getApplication();
                session = (MySession) getSession();
                user = session.getUser();
                ums = session.getUms();
                accessControlService = session.getAccessControlService();
                AccessMode mode = accessControlService.getMode(roModel.getObject().getUri());
                isPrivate = mode.getMode() == Mode.PRIVATE;
                permissions = accessControlService.getPermissions(model.getObject().getUri());
                editors = new ArrayList<>();
                readers = new ArrayList<>();
        		setOutputMarkupPlaceholderTag(true);
        		Form<Void> form = new Form<Void>("form");
        		add(form);
        		getPriviligiesLists();
        		roles = getRoles();
        		selected = roles.get(0);
        		
        		editorsListView = new ListView<Permission>("editors-list", new PropertyModel<List<Permission>>(this, "editorsList")) {
        		    /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					protected void populateItem(final ListItem<Permission> item) {
        		    	final Permission p =  item.getModelObject();
        		    	Form<String> deleteForm = new Form<String>("delete-form") {
							private static final long serialVersionUID = 1L;

							@Override
        		    		protected void onSubmit() {
								item.remove();
								toBeDeleted = p;
        		    			super.onSubmit();
        		    		}
        		    	};
        		    	deleteForm.add(new Label("editor-name", p.getUserLogin().toString()));
        		    	ProtectedAjaxEventButton deleteButton = new ProtectedAjaxEventButton("delete", deleteForm, AccessControlPanel.this,
        	                    PermissionDeletedClickedEvent.class);
        		    	deleteForm.add(deleteButton);
        		    	item.add(deleteForm);
        		    }
        		};
				readersListView = new ListView<Permission>("readers-list", new PropertyModel<List<Permission>>(this, "readersList")) {
        		    /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					protected void populateItem(final ListItem<Permission> item) {
        		    	final Permission p = item.getModelObject();
        		    	Form<String> deleteForm = new Form<String>("delete-form") {
							private static final long serialVersionUID = 1L;

							@Override
        		    		protected void onSubmit() {
								item.remove();
								toBeDeleted = p;
        		    			super.onSubmit();
        		    		}
        		    	};
        		    	deleteForm.add(new Label("reader-name", p.getUserLogin().toString()));
        		    	ProtectedAjaxEventButton deleteButton = new ProtectedAjaxEventButton("delete", deleteForm, AccessControlPanel.this,
        		    			PermissionDeletedClickedEvent.class);
        		    	deleteForm.add(deleteButton);
        		    	item.add(deleteForm);
        		    }
        		};
        		form.add(editorsListView);
        		form.add(readersListView);
        		feedbackPanel = new MyFeedbackPanel("permissionsfeedbackPanel");
                feedbackPanel.setOutputMarkupId(true);
        		feedbackPanel.clearOriginalDestination();
                form.add(feedbackPanel);
                
        		form.add(new ProtectedAjaxEventButton("grant-priviliage", form, this,
        				GrantPermissionClickedEvent.class));
        		
        		choices = new DropDownChoice<Role>("choices", new PropertyModel<Role>(this, "selected"), roles);
   
        		formContainer = new WebMarkupContainer("add-new-privilaga-container");
        		formContainer.setOutputMarkupId(true);
        		useruri = (new RequiredTextField<URI>("useruri"));
        		formContainer.add(useruri);
        		formContainer.add(new ProtectedAjaxEventButton("apply", form, AccessControlPanel.this,
                        PermissionApplyClickedEvent.class).setDefaultFormProcessing(false));
        		formContainer.add(new ProtectedAjaxEventButton("cancel", form, AccessControlPanel.this,
        				PermissionCancelClickedEvent.class).setDefaultFormProcessing(false));
        		formContainer.add(choices);
        		formContainer.setVisible(false);
        		
        		AjaxCheckBox cb = new AjaxCheckBox("private", new PropertyModel<Boolean>(this, "isPrivate")) {
			
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						AccessMode mode = new AccessMode();
						if(isPrivate) {
							mode.setMode(Mode.PRIVATE);
						} else {
							mode.setMode(Mode.PUBLIC);
						}
						mode.setRo(roModel.getObject().getUri().toString());
						accessControlService.setMode(mode);
					}
				};
     
        		form.add(cb);
        		form.add(formContainer);
        }
        
        @Override
        public void onEvent(IEvent<?> event) {
        	if (event.getPayload() instanceof PermissionDeletedClickedEvent) {
        		accessControlService.delete(toBeDeleted);
        		PermissionDeletedClickedEvent d = (PermissionDeletedClickedEvent) event.getPayload();
        		d.getTarget().add(this);
        	}
        	if (event.getPayload() instanceof GrantPermissionClickedEvent) {
        		formContainer.setVisible(true);
        		GrantPermissionClickedEvent g = (GrantPermissionClickedEvent) event.getPayload();
        		useruri.clearInput();
        		g.getTarget().add(this);
        	}
        	if (event.getPayload() instanceof PermissionCancelClickedEvent) {
        		formContainer.setVisible(false);
        		PermissionCancelClickedEvent g = (PermissionCancelClickedEvent) event.getPayload();
        		useruri.clearInput();
        		g.getTarget().add(this);
        	}
        	if (event.getPayload() instanceof PermissionApplyClickedEvent) {
        		PermissionApplyClickedEvent g = (PermissionApplyClickedEvent) event.getPayload();
        		//access control add new permission
        		//reload lists
        		Permission p = new Permission();
        		p.setRo(roModel.getObject().getUri().toString());
        		p.setUserLogin(useruri.getInput().trim());
        		p.setRole(roles.get(Integer.parseInt(choices.getInput())));
        		ClientResponse response = accessControlService.grantPermission(p);
        		int status = response.getStatus();
        		formContainer.setVisible(false);
        		if(status != 201) {
        			feedbackPanel.error("Permission cannot be granted");
        			feedbackPanel.error(response.getEntity(String.class));
        		}
        		g.getTarget().add(this);
        	}
        	
        }
        
        private void getPriviligiesLists() {
        	for (Permission p : permissions) {
        		if(p.getRole().equals(Role.READER)) {
        			readers.add(p);
        		} else if (p.getRole().equals(Role.EDITOR)) {
        			editors.add(p);
        		}
        	}
        }
        
        public List<Permission> getEditorsList() {
            permissions = accessControlService.getPermissions(roModel.getObject().getUri());
            editors = new ArrayList<Permission>();
            getPriviligiesLists();
        	return editors;
        }
        public List<Permission> getReadersList() {
            permissions = accessControlService.getPermissions(roModel.getObject().getUri());
            readers = new ArrayList<Permission>();
            getPriviligiesLists();
        	return readers;
        }
        
        public List<Role> getRoles() {
        	List<Role> roles = new ArrayList<Role>();
    		roles.add(Role.EDITOR);
    		roles.add(Role.READER);
    		return roles;
        }
        
}