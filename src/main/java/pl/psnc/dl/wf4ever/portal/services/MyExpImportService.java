/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.portal.model.Vocab;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.BaseResource;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.InternalPackItem;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.InternalPackItemHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.Pack;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.PackHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.ResourceHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.SimpleResource;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.SimpleResourceHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.User;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel.ImportStatus;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class MyExpImportService
{

	public static void startImport(ImportModel model, URI rodlURI, Token myExpAccessToken, Token dLibraAccessToken,
			String consumerKey, String consumerSecret)
	{
		new ImportThread(model, rodlURI, myExpAccessToken, dLibraAccessToken, consumerKey, consumerSecret).start();
	}


	/**
	 * @param user
	 * @param service
	 * @return
	 * @throws OAuthException
	 * @throws JAXBException
	 */
	public static User retrieveMyExpUser(Token accessToken, OAuthService service)
		throws OAuthException, JAXBException
	{
		User myExpUser;
		Response response = OAuthHelpService.sendRequest(service, Verb.GET, MyExpApi.WHOAMI_URL, accessToken);
		myExpUser = createMyExpUserModel(response.getBody());

		response = OAuthHelpService.sendRequest(service, Verb.GET,
			URI.create(String.format(MyExpApi.GET_USER_URL_TMPL, myExpUser.getId())), accessToken);
		myExpUser = createMyExpUserModel(response.getBody());
		return myExpUser;
	}


	private static User createMyExpUserModel(String xml)
		throws JAXBException
	{
		JAXBContext jc = JAXBContext.newInstance(User.class);

		Unmarshaller u = jc.createUnmarshaller();
		StringBuffer xmlStr = new StringBuffer(xml);
		return (User) u.unmarshal(new StreamSource(new StringReader(xmlStr.toString())));
	}

	private static class ImportThread
		extends Thread
	{

		private static final Logger log = Logger.getLogger(ImportThread.class);

		private final OAuthService service;

		private final ImportModel model;

		private final Token myExpToken;

		private final Token dLibraToken;

		private int stepsTotal = 0;

		private int stepsComplete = 0;

		private URI researchObjectURI;

		private OntModel manifest;

		private final List<String> errors = new ArrayList<>();

		/**
		 * bodyURI, bodyRDF
		 */
		private final Map<URI, Model> annBodies = new HashMap<>();

		/**
		 * targetURI, bodyURI
		 */
		private final Map<URI, URI> annotations = new HashMap<>();

		/**
		 * targetURI, creator name
		 */
		private final Map<URI, URI> creators = new HashMap<>();

		private URI rodlURI;


		public ImportThread(ImportModel importModel, URI rodlURI, Token myExpAccessToken, Token dLibraToken,
				String consumerKey, String consumerSecret)
		{
			super();
			model = importModel;
			this.rodlURI = rodlURI;
			myExpToken = myExpAccessToken;
			this.dLibraToken = dLibraToken;
			service = MyExpApi.getOAuthService(consumerKey, consumerSecret);
		}


		@Override
		public void run()
		{
			model.setStatus(ImportStatus.RUNNING);
			model.setMessage("Preparing the data");

			//			try {
			List<Pack> packs = getPacks(model.getSelectedPacks());
			if (model.getCustomPackId() != null) {
				try {
					packs.add(getPack(model.getCustomPackId()));
				}
				catch (Exception e) {
					log.error("Preparing packs", e);
					errors.add(String.format("When fetching pack with ID %s: %s", model.getCustomPackId(),
						e.getMessage()));
				}
			}
			int simpleResourcesCnt = model.getSelectedFiles().size() + model.getSelectedWorkflows().size();
			for (Pack pack : packs) {
				simpleResourcesCnt += pack.getResources().size();
			}
			stepsTotal = simpleResourcesCnt * 4 + packs.size() * 2 + 3;

			try {
				researchObjectURI = createRO(rodlURI, model.getRoId());
			}
			catch (Exception e) {
				log.error("Creating RO", e);
				errors.add(String.format("When creating RO: %s", e.getMessage()));
				model.setProgressInPercent(100);
				model.setStatus(ImportStatus.FAILED);
			}
			if (researchObjectURI != null) {
				importSimpleResources(model.getSelectedFiles());
				importSimpleResources(model.getSelectedWorkflows());
				importPacks(packs);
				manifest = getManifest(rodlURI);
				updateManifest();
				uploadAnnotations();
				model.setProgressInPercent(100);
				model.setStatus(ImportStatus.FINISHED);
			}
			String finalMessage;
			if (model.getStatus() == ImportStatus.FINISHED) {
				finalMessage = "Import finished successfully!";
			}
			else {
				finalMessage = "Import failed.";
			}
			if (!errors.isEmpty()) {
				finalMessage = finalMessage.concat("<br/>Some errors occurred:<br/><ul>");
				for (String error : errors) {
					finalMessage = finalMessage.concat("<br/><li>").concat(error).concat("</li>");
				}
				finalMessage = finalMessage.concat("</ul>");
			}
			model.setMessage(finalMessage);
		}


		private URI createRO(URI rodlURI, String roId)
			throws Exception
		{
			model.setMessage(String.format("Creating a Research Object \"%s\"", roId));
			ClientResponse r = ROSRService.createResearchObject(rodlURI, roId, dLibraToken);
			if (r.getStatus() != HttpServletResponse.SC_CREATED) {
				throw new Exception("Error: " + r.getClientResponseStatus());
			}
			incrementStepsComplete();
			return r.getLocation();
		}


		private OntModel getManifest(URI rodlURI)
		{
			model.setMessage("Downloading the manifest");
			OntModel manifest = ROSRService.createManifestModel(researchObjectURI);
			incrementStepsComplete();
			return manifest;
		}


		private void updateManifest()
		{
			model.setMessage("Updating the manifest");
			for (Entry<URI, URI> e : annotations.entrySet()) {
				try {
					URI annURI = ROSRService.createAnnotationURI(manifest, researchObjectURI);
					ROSRService.addAnnotationToManifestModel(manifest, researchObjectURI, annURI, e.getKey(),
						e.getValue(), creators.get(e.getKey()));
				}
				catch (Exception ex) {
					log.error("When adding annotation", ex);
					errors.add(String.format("When adding annotation for %s: %s", e.getKey(), ex.getMessage()));
				}
			}
			ROSRService.uploadManifestModel(researchObjectURI, manifest, dLibraToken);
			incrementStepsComplete();
		}


		private void uploadAnnotations()
		{
			for (Entry<URI, Model> e : annBodies.entrySet()) {
				model.setMessage(String.format("Uploading annotation body %s", e.getKey()));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				e.getValue().write(out);
				URI annBodyURI = e.getKey();
				ROSRService.uploadResource(annBodyURI, new ByteArrayInputStream(out.toByteArray()),
					"application/rdf+xml", dLibraToken);
				incrementStepsComplete();
			}
		}


		private void importSimpleResources(List< ? extends SimpleResourceHeader> resourceHeaders)
		{
			for (SimpleResourceHeader header : resourceHeaders) {
				try {
					SimpleResource r = importSimpleResource(header, header.getResourceClass());
					downloadResourceMetadata(r);
				}
				catch (Exception e) {
					log.error("When importing simple resource " + header.getResource(), e);
					errors.add(String.format("When importing %s: %s", header.getResource(), e.getMessage()));
				}
			}
		}


		private List<Pack> getPacks(List<PackHeader> packHeaders)
		{
			List<Pack> packs = new ArrayList<Pack>();
			for (PackHeader packHeader : packHeaders) {
				try {
					packs.add((Pack) getResource(packHeader, Pack.class));
				}
				catch (Exception e) {
					log.error("Preparing packs", e);
					errors.add(String.format("When fetching pack %s: %s", packHeader.getResource().toString(),
						e.getMessage()));
				}
			}
			return packs;
		}


		private Pack getPack(String customPackId)
			throws OAuthException, JAXBException
		{
			PackHeader packHeader = new PackHeader();
			packHeader.setUri(URI.create("http://www.myexperiment.org/pack.xml?id=" + customPackId));
			return (Pack) getResource(packHeader, Pack.class);
		}


		/**
		 * @param roName
		 * @param packs
		 * @param model
		 * @param ro
		 * @param myExpToken
		 * @param dLibraUser
		 * @throws JAXBException
		 * @throws Exception
		 */
		private void importPacks(List<Pack> packs)
		{
			for (Pack pack : packs) {
				try {
					downloadResourceMetadata(pack);

					for (InternalPackItemHeader packItemHeader : pack.getResources()) {
						try {
							importInternalPackItem(pack, packItemHeader);
						}
						catch (Exception e) {
							log.error("When importing internal pack item " + packItemHeader.getResource(), e);
							errors.add(String.format("When importing %s: %s", packItemHeader.getResource(),
								e.getMessage()));
						}
					}
				}
				catch (Exception e) {
					log.error("When importing pack metadata " + pack.getResource(), e);
					errors.add(String.format("When importing %s metadata: %s", pack.getResource(), e.getMessage()));
				}
			}
		}


		/**
		 * @param model
		 * @param ro
		 * @param myExpToken
		 * @param user
		 * @param pack
		 * @param r
		 * @throws JAXBException
		 * @throws Exception
		 */
		private void importInternalPackItem(Pack pack, InternalPackItemHeader packItemHeader)
			throws JAXBException, Exception
		{
			InternalPackItem internalItem = (InternalPackItem) getResource(packItemHeader, InternalPackItem.class);
			SimpleResourceHeader resourceHeader = internalItem.getItem();
			SimpleResource r = importSimpleResource(resourceHeader, resourceHeader.getResourceClass());
			downloadResourceMetadata(r);
		}


		private SimpleResource importSimpleResource(SimpleResourceHeader res,
				Class< ? extends SimpleResource> resourceClass)
			throws Exception
		{
			SimpleResource r = (SimpleResource) getResource(res, resourceClass);
			incrementStepsComplete();

			model.setMessage(String.format("Uploading %s", r.getFilename()));
			ROSRService.uploadResource(researchObjectURI.resolve(r.getFilenameURI()),
				new ByteArrayInputStream(r.getContentDecoded()), r.getContentType(), dLibraToken);

			incrementStepsComplete();
			return r;
		}


		/**
		 * @param res
		 * @param path
		 * @param resourceClass
		 * @return
		 * @throws OAuthException
		 * @throws JAXBException
		 */
		private BaseResource getResource(ResourceHeader res, Class< ? extends BaseResource> resourceClass)
			throws OAuthException, JAXBException
		{
			model.setMessage(String.format("Downloading %s", res.getResourceUrl()));
			Response response = OAuthHelpService.sendRequest(service, Verb.GET, res.getResourceUrl(), myExpToken);
			BaseResource r = (BaseResource) createMyExpResource(response.getBody(), resourceClass);
			return r;
		}


		private void downloadResourceMetadata(BaseResource res)
			throws Exception
		{
			model.setMessage(String.format("Downloading metadata file %s", res.getResource()));
			Response response = OAuthHelpService.sendRequest(service, Verb.GET, res.getResource(), myExpToken,
				"application/rdf+xml");
			// in the future, the RDF could be parsed (and somewhat validated)
			// and the filename can be extracted from it
			String rdf = response.getBody();
			URI annTargetURI;
			if (res instanceof SimpleResource) {
				annTargetURI = researchObjectURI.resolve(((SimpleResource) res).getFilenameURI());
			}
			else {
				annTargetURI = researchObjectURI;
			}
			URI bodyURI = ROSRService.createAnnotationBodyURI(researchObjectURI, annTargetURI);
			annBodies.put(bodyURI, createAnnotationBody(annTargetURI, rdf));
			annotations.put(annTargetURI, bodyURI);
			creators.put(annTargetURI, getResourceAuthor(rdf));

			incrementStepsComplete();
		}


		private static Object createMyExpResource(String xml, Class< ? extends BaseResource> resourceClass)
			throws JAXBException
		{
			JAXBContext jc = JAXBContext.newInstance(resourceClass);
			Unmarshaller u = jc.createUnmarshaller();
			StringBuffer xmlStr = new StringBuffer(xml);
			return u.unmarshal(new StreamSource(new StringReader(xmlStr.toString())));
		}


		private void incrementStepsComplete()
		{
			stepsComplete++;
			model.setProgressInPercent((int) Math.round((double) stepsComplete / stepsTotal * 100));
		}

	}


	static Model createAnnotationBody(URI targetURI, String myExperimentRDF)
		throws UnsupportedEncodingException
	{
		OntModel me = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		me.read(new ByteArrayInputStream(myExperimentRDF.getBytes("UTF-8")), null);
		Model body = ModelFactory.createDefaultModel();

		Resource target = body.createResource(targetURI.toString());

		// source
		Resource source = me.listObjectsOfProperty(Vocab.foafPrimaryTopic).next().asResource();
		target.addProperty(DCTerms.source, source);

		// title
		if (source.hasProperty(DCTerms.title))
			target.addProperty(DCTerms.title, source.getProperty(DCTerms.title).getLiteral());

		// description
		if (source.hasProperty(DCTerms.description))
			target.addProperty(DCTerms.description, source.getProperty(DCTerms.description).getLiteral());

		// creator
		Property owner = me.createProperty("http://rdfs.org/sioc/ns#has_owner");
		if (source.hasProperty(owner))
			target.addProperty(DCTerms.creator, source.getPropertyResourceValue(owner));
		return body;
	}


	static URI getResourceAuthor(String myExperimentRDF)
		throws UnsupportedEncodingException, URISyntaxException
	{
		OntModel me = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		me.read(new ByteArrayInputStream(myExperimentRDF.getBytes("UTF-8")), null);

		Resource source = me.listObjectsOfProperty(Vocab.foafPrimaryTopic).next().asResource();

		// creator
		Property owner = me.createProperty("http://rdfs.org/sioc/ns#has_owner");
		if (source.hasProperty(owner)) {
			Resource user = source.getPropertyResourceValue(owner);
			return new URI(user.getURI());
		}
		return null;
	}

}