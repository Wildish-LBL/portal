package pl.psnc.dl.wf4ever.portal.services;

import java.net.HttpURLConnection;
import java.net.URI;

import org.apache.log4j.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Utility service for OAuth communication.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public final class OAuthHelpService {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(OAuthHelpService.class);


    /**
     * Constructor.
     */
    private OAuthHelpService() {
        // nope
    }


    /**
     * Sends an unsigned request with no body.
     * 
     * @param service
     *            OAuth service
     * @param verb
     *            HTTP method
     * @param uri
     *            URL to call
     * @return a response
     * @throws OAuthException
     *             a OAuth problem
     */
    public static Response sendRequest(OAuthService service, Verb verb, URI uri)
            throws OAuthException {
        return sendRequest(service, verb, uri, null);
    }


    /**
     * Executes a request with no body.
     * 
     * @param service
     *            OAuth service
     * @param verb
     *            HTTP method
     * @param uri
     *            URL to call
     * @param token
     *            access token
     * @return a response
     * @throws OAuthException
     *             a OAuth problem
     */
    public static Response sendRequest(OAuthService service, Verb verb, URI uri, Token token)
            throws OAuthException {
        OAuthRequest request = new OAuthRequest(verb, uri.toString());
        if (token != null) {
            service.signRequest(token, request);
        }
        Response response = request.send();
        validateResponseCode(verb, response);
        return response;
    }


    /**
     * Executes a request with no body but with content negotiation. Makes sense to use only with GET.
     * 
     * @param service
     *            OAuth service
     * @param verb
     *            HTTP method
     * @param uri
     *            URL to call
     * @param token
     *            access token
     * @param accept
     *            Accept header
     * @return a response
     * @throws OAuthException
     *             a OAuth problem
     */
    public static Response sendRequest(OAuthService service, Verb verb, URI uri, Token token, String accept)
            throws OAuthException {
        if (verb != Verb.GET) {
            LOG.warn("Using accept header with " + verb + " request.");
        }
        OAuthRequest request = new OAuthRequest(verb, uri.toString());
        request.addHeader("Accept", accept);
        if (token != null) {
            service.signRequest(token, request);
        }
        Response response = request.send();
        validateResponseCode(verb, response);
        return response;
    }


    /**
     * Executes a request with body, content type must also be specified.
     * 
     * @param service
     *            OAuth service
     * @param verb
     *            HTTP method
     * @param uri
     *            URL to call
     * @param token
     *            access token
     * @param payload
     *            body
     * @param contentType
     *            Content-type header
     * @return a response
     * @throws OAuthException
     *             a OAuth problem
     */
    public static Response sendRequest(OAuthService service, Verb verb, URI uri, Token token, byte[] payload,
            String contentType)
            throws OAuthException {
        OAuthRequest request = new OAuthRequest(verb, uri.toString());
        request.addPayload(payload);
        request.addHeader("Content-type", contentType);
        if (token != null) {
            service.signRequest(token, request);
        }
        Response response = request.send();
        validateResponseCode(verb, response);
        return response;
    }


    /**
     * Check the OAuth response.
     * 
     * @param verb
     *            HTTP method
     * @param response
     *            the response
     * @throws OAuthException
     *             the response is incorrect
     */
    private static void validateResponseCode(Verb verb, Response response)
            throws OAuthException {
        switch (verb) {
            case GET:
                if (response.getCode() != HttpURLConnection.HTTP_OK) {
                    throw prepareException(response);
                }
                break;
            case PUT:
                if (response.getCode() != HttpURLConnection.HTTP_OK
                        && response.getCode() != HttpURLConnection.HTTP_CREATED) {
                    throw prepareException(response);
                }
                break;
            case POST:
                if (response.getCode() != HttpURLConnection.HTTP_CREATED) {
                    throw prepareException(response);
                }
                break;
            case DELETE:
                if (response.getCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                    throw prepareException(response);
                }
                break;
            default:
                break;
        }
    }


    /**
     * Create an OAuth exception.
     * 
     * @param response
     *            response
     * @return exception
     */
    private static OAuthException prepareException(Response response) {
        if (response.getCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            return new OAuthException(response, "the access token has been rejected");
        }
        return new OAuthException(response);
    }
}
