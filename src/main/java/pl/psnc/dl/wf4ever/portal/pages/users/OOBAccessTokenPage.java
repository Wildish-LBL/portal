package pl.psnc.dl.wf4ever.portal.pages.users;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.User;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;

import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * This page displayes the OOB access token to the user.
 * 
 * In the future, it should rather display the one-time authorization code.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class OOBAccessTokenPage extends Base {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(OOBAccessTokenPage.class);


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters
     */
    public OOBAccessTokenPage(PageParameters pageParameters) {
        super(pageParameters);

        MySession session = (MySession) getSession();
        User user = session.getUser();
        String clientId = pageParameters.get("clientId").toString();
        String clientName = pageParameters.get("clientName").toString();
        String token;

        try {
            token = session.getUms().createAccessToken(user.getURI().toString(), clientId);
        } catch (UniformInterfaceException e) {
            LOG.error(e.getResponse().getClientResponseStatus().toString());
            error(e.getResponse().getClientResponseStatus().toString());
            token = "--";
        }

        add(new Label("token", token));
        add(new Label("clientName", clientName));
        add(new BookmarkablePageLink<Void>("back", AccessTokensPage.class));
    }
}
