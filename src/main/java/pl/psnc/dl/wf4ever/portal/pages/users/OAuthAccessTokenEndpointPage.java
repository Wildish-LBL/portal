/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.users;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.common.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.users.AuthCodeData;
import pl.psnc.dl.wf4ever.portal.services.HibernateService;

import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * This is the OAuth 2.0 access token endpoint, it exchanges authorization code for access token.
 * 
 * @author Piotr Hołubowicz
 */
public class OAuthAccessTokenEndpointPage extends WebPage {

    /** id. */
    private static final long serialVersionUID = 3793214124123802219L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(OAuthAccessTokenEndpointPage.class);

    /** Response body. */
    private String json;

    /** Response status. */
    private int status;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameter
     */
    public OAuthAccessTokenEndpointPage(PageParameters pageParameters) {
        super(pageParameters);

        prepareResponse(pageParameters);

        getRequestCycle().replaceAllRequestHandlers(new IRequestHandler() {

            @Override
            public void respond(IRequestCycle requestCycle) {
                WebResponse response = ((WebResponse) requestCycle.getResponse());
                response.setStatus(status);
                response.setContentType("application/json;charset=UTF-8");
                response.addHeader("Cache-control", "no-store");
                response.addHeader("Pragma", "no-cache");
                response.write(json);
                LOG.debug("Returning access token: " + json);
            }


            @Override
            public void detach(IRequestCycle arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


    /**
     * Prepare the response, generate the access token.
     * 
     * @param pageParameters
     *            page parameters with the request details
     */
    private void prepareResponse(PageParameters pageParameters) {
        String error = null;
        String errorDesc = null;
        AuthCodeData data = null;
        if (pageParameters.get("grant_type") == null || pageParameters.get("code") == null) {
            error = "invalid_request";
            errorDesc = "Grant type or code missing";
        } else if (!pageParameters.get("grant_type").toString().equals("authorization_code")) {
            error = "unsupported_grant_type";
            errorDesc = "grant type: " + pageParameters.get("grant_type").toString();
        } else {
            String code = pageParameters.get("code").toString();
            data = HibernateService.loadCode(code);
            if (data == null) {
                error = "invalid_grant";
                errorDesc = "Code " + code + " is not valid";
            } else if (data.getProvidedRedirectURI() != null
                    && (pageParameters.get("redirect_uri") == null || !pageParameters.get("redirect_uri").toString()
                            .equals(data.getProvidedRedirectURI()))) {
                error = "invalid_grant";
                errorDesc = "Redirect URI is not valid";
            }

        }
        if (error != null) {
            json = String.format("{\"error\": \"%s\", \"error_description\": \"%s\"}", error, errorDesc);
            status = 400;
        } else {
            try {
                PortalApplication app = ((PortalApplication) getApplication());
                try {
                    String token = UserManagementService.createAccessToken(app.getRodlURI(), app.getAdminToken(),
                        data.getUserId(), data.getClientId());
                    json = String.format("{\"access_token\": \"%s\", \"token_type\": \"bearer\"}", token);
                    status = 200;
                    HibernateService.deleteCode(data);
                } catch (UniformInterfaceException e) {
                    json = String.format("{\"error\": \"invalid_request\", \"error_description\": \"%s\"}", e
                            .getResponse().getClientResponseStatus());
                    status = 500;
                    e.getResponse().close();
                }
            } catch (Exception e) {
                json = String.format("{\"error\": \"invalid_request\", \"error_description\": \"%s\"}", e.getMessage());
                status = 500;
            }
        }

    }
}
