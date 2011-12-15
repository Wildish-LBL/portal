/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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
	 * @throws UnsupportedEncodingException 
	 * @throws OAuthException 
	 * @throws Exception if ROSRS doesn't return 201 Created (or 409 if ignoreIfExists is true)
	 */
	public static boolean createResearchObject(String roId, Token dLibraToken, boolean ignoreIfExists)
		throws UnsupportedEncodingException, OAuthException
	{
		try {
			OAuthHelpService.sendRequest(dLibraService, Verb.POST, createROsURL(), dLibraToken, roId.getBytes("UTF-8"),
				"text/plain");
		}
		catch (OAuthException e) {
			if (e.getResponse().getCode() == HttpURLConnection.HTTP_CONFLICT && ignoreIfExists) {
				return false;
			}
			else {
				throw e;
			}
		}
		return true;
	}


	public static InputStream getResource(URI resourceURI)
		throws OAuthException
	{
		return OAuthHelpService.sendRequest(dLibraService, Verb.GET, resourceURI).getStream();
	}


	public static void sendResource(String path, String roId, byte[] content, String contentType, Token dLibraToken)
		throws Exception
	{
		OAuthHelpService.sendRequest(dLibraService, Verb.PUT, createResourceURL(roId, path), dLibraToken, content,
			contentType != null ? contentType : "text/plain");
	}


	private static URI createROsURL()
	{
		try {
			return new URI(URI_SCHEME, URI_HOST, URI_ROS, null);
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	@SuppressWarnings("unused")
	private static URI createROIdURL(String roId)
	{
		try {
			String path = String.format(URI_RO_ID, roId);
			return new URI(URI_SCHEME, URI_HOST, path, null);
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	private static URI createResourceURL(String roId, String resource)
	{
		try {
			String path = String.format(URI_RESOURCE, roId, resource);
			return new URI(URI_SCHEME, URI_HOST, path, null);
		}
		catch (Exception e) {
			log.error(e);
			return null;
		}
	}


	public static List<URI> getROList()
		throws Exception
	{
		return getROList(null);
	}


	public static List<URI> getROList(Token dLibraToken)
		throws MalformedURLException, OAuthException, URISyntaxException
	{
		Response response;
		if (dLibraToken == null) {
			response = OAuthHelpService.sendRequest(dLibraService, Verb.GET, new URI(URI_SCHEME, URI_HOST, URI_ROS,
					null));
		}
		else {
			response = OAuthHelpService.sendRequest(dLibraService, Verb.GET, new URI(URI_SCHEME, URI_HOST, URI_ROS,
					null), dLibraToken);
		}
		List<URI> uris = new ArrayList<URI>();
		for (String s : response.getBody().split("[\\r\\n]+")) {
			uris.add(new URI(s));
		}
		return uris;
	}
}
