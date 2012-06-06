package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * This page redirects to the User Management Application. This works as Wicket's authentication page in its
 * authorization mechanism.
 * 
 * @author piotrhol
 * 
 */
public class AuthenticatePage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = -8396464259446612570L;

    /** The UMA authorization page URL. */
    private static String authorizationURL;


    /**
     * Constructor.
     */
    public AuthenticatePage() {
        super(new PageParameters().add("redirectTo", authorizationURL));
    }


    public static String getAuthorizationURL() {
        return authorizationURL;
    }


    public static void setAuthorizationURL(String authorizationURL) {
        AuthenticatePage.authorizationURL = authorizationURL;
    }
}
