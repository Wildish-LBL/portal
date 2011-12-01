/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.portal.myexpimport.model.InternalPackItem;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.InternalPackItemHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.Pack;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.PackHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.Resource;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.ResourceHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.SimpleResource;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.SimpleResourceHeader;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.User;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel.ImportStatus;
import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel.WorkspaceType;

/**
 * @author Piotr Hołubowicz
 *
 */
public class MyExpImportService
{

	public static void startImport(ImportModel model, Token myExpAccessToken, Token dLibraAccessToken,
			String consumerKey, String consumerSecret)
	{
		new ImportThread(model, myExpAccessToken, dLibraAccessToken, consumerKey, consumerSecret).start();
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
			String.format(MyExpApi.GET_USER_URL, myExpUser.getId()), accessToken);
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


		public ImportThread(ImportModel importModel, Token myExpAccessToken, Token dLibraToken, String consumerKey,
				String consumerSecret)
		{
			super();
			model = importModel;
			myExpToken = myExpAccessToken;
			this.dLibraToken = dLibraToken;
			service = MyExpApi.getOAuthService(consumerKey, consumerSecret);
		}


		@Override
		public void run()
		{
			model.setStatus(ImportStatus.RUNNING);
			model.setMessage("Preparing the data");
			try {
				List<Pack> packs = getPacks(model.getSelectedPacks());
				stepsTotal = model.getSelectedFiles().size() + model.getSelectedWorkflows().size();
				for (Pack pack : packs) {
					stepsTotal += pack.getResources().size();
				}

				if (model.getWorkspaceType() == WorkspaceType.NEW) {
					createWorkspace(model.getWorkspaceId());
				}
				try {
					createRO(model.getRoName());
					importSimpleResources(model.getSelectedFiles(), model.getRoName());
					importSimpleResources(model.getSelectedWorkflows(), model.getRoName());
					importPacks(packs, model.getRoName());
				}
				catch (Exception e) {
					log.error("Error during import", e);
					model.setMessage(e.getMessage());
				}
			}
			catch (Exception e1) {
				log.error("Error when creating workspace", e1);
				model.setMessage(e1.getMessage());
			}
			model.setMessage("Finished");
			model.setStatus(ImportStatus.FINISHED);
		}


		/**
		 * @param model
		 * @param ro
		 * @param dLibraUser
		 * @throws Exception
		 */
		private void createWorkspace(String workspaceId)
			throws Exception
		{
			model.setMessage(String.format("Creating a workspace \"%s\"", workspaceId));
			if (!DlibraService.createWorkspace(workspaceId, dLibraToken)) {
				model.setMessage("Merged with an existing workspace");
			}
		}


		/**
		 * @param model
		 * @param ro
		 * @param myExpToken
		 * @param dLibraUser
		 * @throws JAXBException
		 * @throws Exception
		 */
		private void importSimpleResources(List< ? extends SimpleResourceHeader> resourceHeaders, String roName)
			throws JAXBException, Exception
		{
			for (SimpleResourceHeader header : resourceHeaders) {
				SimpleResource r = importSimpleResource(header, roName, "", header.getResourceClass());
				importResourceMetadata(r, r.getFilename() + ".rdf", roName, "");
			}
		}


		private List<Pack> getPacks(List<PackHeader> packHeaders)
			throws OAuthException, JAXBException
		{
			List<Pack> packs = new ArrayList<Pack>();
			for (PackHeader packHeader : packHeaders) {
				packs.add((Pack) getResource(packHeader, Pack.class));
			}
			return packs;
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
		private void importPacks(List<Pack> packs, String roName)
			throws JAXBException, Exception
		{
			for (Pack pack : packs) {
				importResourceMetadata(pack, pack.getId() + ".rdf", roName, "");

				for (InternalPackItemHeader packItemHeader : pack.getResources()) {
					importInternalPackItem(pack, packItemHeader);
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
			importSimpleResource(resourceHeader, model.getRoName(), pack.getId() + "/",
				resourceHeader.getResourceClass());
		}


		private SimpleResource importSimpleResource(SimpleResourceHeader res, String roName, String path,
				Class< ? extends SimpleResource> resourceClass)
			throws Exception
		{
			SimpleResource r = (SimpleResource) getResource(res, resourceClass);

			String filename = path + r.getFilename();
			model.setMessage(String.format("Uploading %s", filename));
			DlibraService.sendResource(model.getWorkspaceId(), filename, roName, r.getContentDecoded(),
				r.getContentType(), dLibraToken);

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
		private Resource getResource(ResourceHeader res, Class< ? extends Resource> resourceClass)
			throws OAuthException, JAXBException
		{
			model.setMessage(String.format("Downloading %s", res.getResourceUrl()));
			Response response = OAuthHelpService.sendRequest(service, Verb.GET, res.getResourceUrl(), myExpToken);
			Resource r = (Resource) createMyExpResource(response.getBody(), resourceClass);
			return r;
		}


		private void importResourceMetadata(Resource res, String filename, String roName, String path)
			throws Exception
		{
			model.setMessage(String.format("Downloading metadata file %s", res.getResource()));
			Response response = OAuthHelpService.sendRequest(service, Verb.GET, res.getResource(), myExpToken,
				"application/rdf+xml");
			// in the future, the RDF could be parsed (and somewhat validated) and the filename can be extracted from it
			String rdf = response.getBody();

			model.setMessage(String.format("Uploading metadata file %s", filename));
			DlibraService.sendResource(model.getWorkspaceId(), filename, roName, rdf.getBytes(), "application/rdf+xml",
				dLibraToken);
		}


		/**
		 * @param model
		 * @param ro
		 * @param dLibraUser
		 * @throws Exception
		 */
		private void createRO(String id)
			throws Exception
		{
			model.setMessage(String.format("Creating a Research Object \"%s\"", id));
			if (!DlibraService.createResearchObjectAndVersion(model.getWorkspaceId(), id, dLibraToken,
				model.isMergeROs())) {
				model.setMessage("Merged with an existing Research Object");
			}
		}


		private static Object createMyExpResource(String xml, Class< ? extends Resource> resourceClass)
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
}