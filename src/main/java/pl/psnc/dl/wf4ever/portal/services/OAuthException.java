/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import org.scribe.model.Response;

/**
 * Exception in OAuth handshake.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class OAuthException extends Exception {

    /** id. */
    private static final long serialVersionUID = 4341885401436426808L;

    /** The problematic response. */
    private Response response;


    /**
     * Constructor.
     * 
     * @param response
     *            the problematic response
     * @param explanation
     *            reason
     */
    public OAuthException(Response response, String explanation) {
        super(String.format("Error %d: %s", response.getCode(), explanation));
        this.response = response;
    }


    /**
     * Constructor.
     * 
     * @param response
     *            the problematic response
     */
    public OAuthException(Response response) {
        super(String.format("Error %d", response.getCode()));
        this.response = response;
    }


    public Response getResponse() {
        return response;
    }

}
