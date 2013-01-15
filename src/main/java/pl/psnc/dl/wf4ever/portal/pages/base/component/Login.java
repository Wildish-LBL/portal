package pl.psnc.dl.wf4ever.portal.pages.base.component;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;

public class Login extends Panel {

    public Login(String id) {
        super(id);
        Label userNameLabel = new Label("username-text", new PropertyModel<String>(this, "usernameText"));
        Label signInLabel = new Label("sign-in-text", new PropertyModel<String>(this, "signInButtonText"));
        Link signInLink = new Link<Void>("sign-in-link") {

            @Override
            public void onClick() {
                if (MySession.get().isSignedIn()) {
                    MySession.get().signOut();
                    throw new RestartResponseException(getApplication().getHomePage());
                } else {
                    throw new RestartResponseException(((PortalApplication) getApplication()).getSignInPageClass());
                }
            }
        };
        signInLink.add(signInLabel);
        add(userNameLabel);
        add(signInLink);
    }


    /**
     * The content of user bar.
     * 
     * @return "" or username
     */
    public String getUsernameText() {
        if (MySession.get().isSignedIn()) {
            return "Logged as " + MySession.get().getUser().getUsername();
        } else {
            return "";
        }
    }


    /**
     * The content of the sign in button.
     * 
     * @return "Sign out" or "Sign in"
     */
    public String getSignInButtonText() {
        if (MySession.get().isSignedIn()) {
            return "sign out";
        } else {
            return "Sign in";
        }
    }


    /**
     * The sign in button tooltip.
     * 
     * @return "Signed in as X" or "Click to sign in!"
     */
    public String getSignInTwipsy() {
        if (MySession.get().isSignedIn()) {
            return "Signed in as " + MySession.get().getUser().getUsername();
        } else {
            return "Click to sign in!";
        }
    }
}
