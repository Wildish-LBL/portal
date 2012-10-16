package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.home.HomePage;
import pl.psnc.dl.wf4ever.portal.pages.my.MyRosPage;
import pl.psnc.dl.wf4ever.portal.pages.users.ProfilePage;

/**
 * The common base of all HTML pages.
 * 
 * @author piotrekhol
 * 
 */
public class TemplatePage extends WebPage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** RODL base URI. */
    protected URI rodlURI = ((PortalApplication) getApplication()).getRodlURI();


    /**
     * Constructor.
     * 
     * TODO: are the redirection params used?
     * 
     * @param parameters
     *            may contain redirection params
     */
    public TemplatePage(final PageParameters parameters) {
        getSession().bind();
        MySession.get().persist();
        final WebMarkupContainer redirect = new WebMarkupContainer("redirect");
        String redirectionURL = parameters.get("redirectTo").toString();
        if (redirectionURL != null) {
            int redirectionDelay = parameters.get("redirectDelay").toInt(3);
            final String content = "" + redirectionDelay + ";URL=" + redirectionURL;
            redirect.add(new AttributeModifier("content", new Model<String>(content)));
            parameters.remove("redirectTo");
            parameters.remove("redirectDelay");
        } else {
            redirect.setVisible(false);
        }
        add(redirect);

        add(new BookmarkablePageLink<Void>("baner", HomePage.class));
        add(new BookmarkablePageLink<Void>("banerTitle", HomePage.class));

        add(new BookmarkablePageLink<Void>("menu-home", HomePage.class));
        add(new BookmarkablePageLink<Void>("menu-myros", MyRosPage.class));
        add(new BookmarkablePageLink<Void>("menu-sparql", SparqlEndpointPage.class));
        add(new BookmarkablePageLink<Void>("menu-profile", ProfilePage.class));

        WebMarkupContainer signedInAs = new WebMarkupContainer("signedInAs");
        signedInAs.add(new AttributeModifier("data-original-title", new PropertyModel<String>(this, "signInTwipsy")));
        add(signedInAs);
        signedInAs.add(new AjaxFallbackLink<String>("signIn") {

            private static final long serialVersionUID = -4458301162412620530L;


            @Override
            public void onClick(AjaxRequestTarget target) {
                if (MySession.get().isSignedIn()) {
                    MySession.get().signOut();
                    throw new RestartResponseException(getApplication().getHomePage());
                } else {
                    throw new RestartResponseException(((PortalApplication) getApplication()).getSignInPageClass());
                }
            }

        }.add(new Label("signInText", new PropertyModel<String>(this, "signInButtonText"))));
    }


    /**
     * The content of the sign in button.
     * 
     * @return "Sign out" or "Sign in"
     */
    public String getSignInButtonText() {
        if (MySession.get().isSignedIn()) {
            return "Sign out";
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


    public URI getRodlURI() {
        return rodlURI;
    }

}
