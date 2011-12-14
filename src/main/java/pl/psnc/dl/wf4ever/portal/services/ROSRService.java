/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.util.crypt.Base64;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * @author Piotr Hołubowicz
 *
 */
public class ROSRService
{

	private static final Logger log = Logger.getLogger(ROSRService.class);

	private static final String URI_SCHEME = "http";

	private static final String URI_HOST = "sandbox.wf4ever-project.org";

	private static final String URI_PATH_BASE = "/rosrs5/";

	private static final String URI_ROS = URI_PATH_BASE + "ROs/";

	private static final String URI_RO_ID = URI_ROS + "%s/";

	private static final String URI_RESOURCE = URI_RO_ID + "%s";

	private static final OAuthService dLibraService = DlibraApi.getOAuthService("notused", null);


	/**
	 * Creates a Research Object.
	 * @param roId RO identifier
	 * @param user dLibra user model
	 * @param ignoreIfExists should it finish without throwing exception if ROSRS returns 409?
	 * @return true only if ROSRS returns 201 Created
	 * @throws Exception if ROSRS doesn't return 201 Created (or 409 if ignoreIfExists is true)
	 */
	public static boolean createResearchObject(String roId, Token dLibraToken, boolean ignoreIfExists)
		throws Exception
	{
		String url = createROsURL().toString();
		OAuthRequest request = new OAuthRequest(Verb.POST, url);
		request.addHeader("Content-type", "text/plain");
		request.addPayload(roId);
		dLibraService.signRequest(dLibraToken, request);
		Response response = request.send();
		if (response.getCode() == HttpURLConnection.HTTP_CREATED) {
			return true;
		}
		else if (response.getCode() == HttpURLConnection.HTTP_CONFLICT && ignoreIfExists) {
			return false;
		}
		else {
			throw new Exception("Error when creating RO " + roId + ", response: " + response.getCode() + " "
					+ response.getBody());
		}
	}


	public static void sendResource(String path, String roId, byte[] content, String contentType, Token dLibraToken)
		throws Exception
	{
		String url = createResourceURL(roId, path).toString();
		OAuthRequest request = new OAuthRequest(Verb.PUT, url);
		request.addHeader("Content-Type", contentType != null ? contentType : "text/plain");
		request.addPayload(content);
		dLibraService.signRequest(dLibraToken, request);
		Response response = request.send();
		if (response.getCode() != HttpURLConnection.HTTP_OK) {
			throw new Exception("Error when sending resource " + path + ", response: " + response.getCode() + " "
					+ response.getBody());
		}
	}


	public static Token generateAccessToken(String username, String password)
	{
		String token = Base64.encodeBase64String((username + ":" + password).getBytes());
		token = StringUtils.trim(token);
		log.debug(String.format("Username %s, password %s, access token %s", username, password, token));
		return new Token(token, null);
	}


	private static URL createROsURL()
	{
		try {
			return new URI(URI_SCHEME, URI_HOST, URI_ROS, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URL createResourceURL(String roId, String resource)
	{
		try {
			String path = String.format(URI_RESOURCE, roId, resource);
			return new URI(URI_SCHEME, URI_HOST, path, null).toURL();
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	public static List<URI> getROList()
		throws Exception
	{
		String url = new URI(URI_SCHEME, URI_HOST, URI_ROS, null).toURL().toString();
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		Response response = request.send();
		if (response.getCode() != HttpURLConnection.HTTP_OK) {
			throw new Exception("Error when getting RO list, response: " + response.getCode() + " "
					+ response.getBody());
		}
		List<URI> uris = new ArrayList<URI>();
		for (String s : response.getBody().split("[\\r\\n]+")) {
			uris.add(new URI(s));
		}

		return uris;
	}
}
