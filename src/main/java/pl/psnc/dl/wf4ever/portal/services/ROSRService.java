/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.portal.model.RoFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

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

	private static final String URI_WHOAMI = URI_PATH_BASE + "whoami/";

	private static final OAuthService dLibraService = DlibraApi.getOAuthService("notused", null);


	/**
	 * Creates a Research Object.
	 * 
	 * @param roId
	 *            RO identifier
	 * @param user
	 *            dLibra user model
	 * @param ignoreIfExists
	 *            should it finish without throwing exception if ROSRS returns 409?
	 * @return true only if ROSRS returns 201 Created
	 * @throws UnsupportedEncodingException
	 * @throws OAuthException
	 * @throws Exception
	 *             if ROSRS doesn't return 201 Created (or 409 if ignoreIfExists is true)
	 */
	public static URI createResearchObject(String roId, Token dLibraToken, boolean ignoreIfExists)
		throws UnsupportedEncodingException, OAuthException
	{
		try {
			Response response = OAuthHelpService.sendRequest(dLibraService, Verb.POST, createROsURL(), dLibraToken,
				roId.getBytes("UTF-8"), "text/plain");
			return URI.create(response.getHeader("Location"));
		}
		catch (OAuthException e) {
			if (e.getResponse().getCode() == HttpURLConnection.HTTP_CONFLICT && ignoreIfExists) {
				return null;
			}
			else {
				throw e;
			}
		}
	}


	public static InputStream getResource(URI resourceURI)
		throws OAuthException
	{
		return OAuthHelpService.sendRequest(dLibraService, Verb.GET, resourceURI).getStream();
	}


	public static void sendResource(URI resourceURI, byte[] content, String contentType, Token dLibraToken)
		throws OAuthException

	{
		OAuthHelpService.sendRequest(dLibraService, Verb.PUT, resourceURI, dLibraToken, content,
			contentType != null ? contentType : "text/plain");
	}


	public static String sendResource(URI resourceURI, InputStream content, String contentType, Token dLibraToken)
	{
		Client client = Client.create();
		WebResource webResource = client.resource(resourceURI.toString());
		return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).type(contentType)
				.put(String.class, content);
	}


	public static void deleteResource(URI resourceURI, Token dLibraAccessToken)
		throws OAuthException
	{
		OAuthHelpService.sendRequest(dLibraService, Verb.DELETE, resourceURI, dLibraAccessToken);
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
			if (!s.isEmpty()) {
				uris.add(new URI(s));
			}
		}
		return uris;
	}


	public static void deleteResearchObject(URI researchObjectURI, Token dLibraToken)
		throws OAuthException
	{
		OAuthHelpService.sendRequest(dLibraService, Verb.DELETE, researchObjectURI, dLibraToken);
	}


	/**
	 * Creates an annotation and an empty annotation body in ROSRS
	 * 
	 * @param researchObjectURI
	 * @param targetURI
	 * @param username
	 * @param dLibraToken
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	public static void addAnnotation(URI researchObjectURI, URI targetURI, String username, Token dLibraToken)
		throws OAuthException, URISyntaxException
	{
		InputStream is = ROSRService.getResource(researchObjectURI.resolve(".ro/manifest"));
		OntModel manifest = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		manifest.read(is, null);

		URI bodyURI = ROSRService.createAnnotationBodyURI(researchObjectURI, targetURI);
		addAnnotation(manifest, researchObjectURI, targetURI, bodyURI, username);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		manifest.write(out);
		sendResource(researchObjectURI.resolve(".ro/manifest"), out.toByteArray(), "application/rdf+xml", dLibraToken);

		OntModel body = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		body.write(out2);
		sendResource(bodyURI, out2.toByteArray(), "application/rdf+xml", dLibraToken);
	}


	public static void deleteAnnotation(URI researchObjectURI, URI annURI, Token dLibraToken)
		throws OAuthException, IllegalArgumentException, URISyntaxException
	{
		InputStream is = ROSRService.getResource(researchObjectURI.resolve(".ro/manifest"));
		OntModel manifest = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		manifest.read(is, null);

		Individual ann = manifest.getIndividual(annURI.toString());
		if (ann == null) {
			throw new IllegalArgumentException("Annotation URI is not valid");
		}
		Resource body = ann.getPropertyResourceValue(RoFactory.aoBody);
		try {
			deleteResource(new URI(body.getURI()), dLibraToken);
		}
		catch (OAuthException e) {
			log.warn("Problem with deleting annotation body: " + e.getMessage());
		}

		manifest.removeAll(ann, null, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		manifest.write(out);
		sendResource(researchObjectURI.resolve(".ro/manifest"), out.toByteArray(), "application/rdf+xml", dLibraToken);
	}


	/**
	 * Adds an annotation to the manifest model
	 * 
	 * @param manifest
	 * @param researchObjectURI
	 * @param targetURI
	 * @param bodyURI
	 * @throws URISyntaxException
	 */
	public static void addAnnotation(OntModel manifest, URI researchObjectURI, URI targetURI, URI bodyURI,
			String username)
		throws URISyntaxException
	{
		Individual ann = manifest.createIndividual(createAnnotationURI(manifest, researchObjectURI).toString(),
			RoFactory.aggregatedAnnotation);
		ann.addProperty(RoFactory.annotatesResource, manifest.createResource(targetURI.toString()));
		ann.addProperty(RoFactory.aoBody, manifest.createResource(bodyURI.toString()));
		ann.addProperty(DCTerms.created, manifest.createTypedLiteral(Calendar.getInstance()));
		Individual agent = manifest.createResource(new AnonId(username)).as(Individual.class);
		agent.setOntClass(RoFactory.foafAgent);
		agent.addProperty(RoFactory.foafName, username);
		ann.addProperty(DCTerms.creator, agent);
	}


	/**
	 * 
	 * @param manifest
	 * @param researchObjectURI
	 * @return i.e.
	 *         http://sandbox.wf4ever-project.org/rosrs5/ROs/ann217/52a272f1-864f-4a42
	 *         -89ff-2501a739d6f0
	 */
	private static URI createAnnotationURI(OntModel manifest, URI researchObjectURI)
	{
		URI ann = null;
		do {
			ann = researchObjectURI.resolve(UUID.randomUUID().toString());
		}
		while (manifest.containsResource(manifest.createResource(ann.toString())));
		return ann;
	}


	/**
	 * 
	 * @param researchObjectURI
	 * @param targetURI
	 * @return i.e.
	 *         http://sandbox.wf4ever-project.org/rosrs5/ROs/ann217/.ro/ro--5600459667350895101.
	 *         rdf
	 * @throws URISyntaxException
	 */
	public static URI createAnnotationBodyURI(URI researchObjectURI, URI targetURI)
		throws URISyntaxException
	{
		String targetName;
		if (targetURI.equals(researchObjectURI))
			targetName = "ro";
		else
			targetName = targetURI.resolve(".").relativize(targetURI).toString();
		String randomBit = "" + Math.abs(UUID.randomUUID().getLeastSignificantBits());

		return researchObjectURI.resolve(".ro/" + targetName + "-" + randomBit);
	}


	public static String[] getWhoAmi(Token dLibraToken)
		throws OAuthException, URISyntaxException
	{
		Response response = OAuthHelpService.sendRequest(dLibraService, Verb.GET, new URI(URI_SCHEME, URI_HOST,
				URI_WHOAMI, null), dLibraToken);
		return response.getBody().split("[\\r\\n]+");
	}

}
