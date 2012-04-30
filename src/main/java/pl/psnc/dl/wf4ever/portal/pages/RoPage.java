package pl.psnc.dl.wf4ever.portal.pages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Annotation;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.sun.jersey.api.client.ClientResponse;

public class RoPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(RoPage.class);

	URI roURI;

	Map<URI, AggregatedResource> resources;

	boolean canEdit = false;

	final RoViewerBox roViewerBox;

	final AnnotatingBox annotatingBox;

	private RoTreeModel conceptualResourcesTree;

	private RoTreeModel physicalResourcesTree;

	final StatementEditModal stmtEditForm;

	final RelationEditModal relEditForm;

	/** http://www.colourlovers.com/palette/1473/Ocean_Five */
	public static String[] interactiveViewColors = { "#00A0B0", "#6A4A3C", "#CC333F", "#EB6841", "#EDC951"};

	private UploadResourceModal uploadResourceModal;


	@SuppressWarnings("serial")
	public RoPage(final PageParameters parameters)
		throws URISyntaxException, MalformedURLException, OAuthException
	{
		super(parameters);
		if (!parameters.get("ro").isEmpty()) {
			roURI = new URI(UrlDecoder.QUERY_INSTANCE.decode(parameters.get("ro").toString(), "UTF-8"));
		}
		else {
			throw new RestartResponseException(ErrorPage.class, new PageParameters().add("message",
				"The RO URI is missing"));
		}

		if (MySession.get().isSignedIn()) {
			List<URI> uris = ROSRService.getROList(MySession.get().getdLibraAccessToken());
			canEdit = uris.contains(roURI);
		}
		add(new Label("title", roURI.toString()));

		final CompoundPropertyModel<AggregatedResource> itemModel = new CompoundPropertyModel<AggregatedResource>(
				(AggregatedResource) null);
		roViewerBox = new RoViewerBox(this, itemModel, new PropertyModel<TreeModel>(this, "conceptualResourcesTree"),
				new PropertyModel<TreeModel>(this, "physicalResourcesTree"), "loadingROFragment");
		add(roViewerBox);
		annotatingBox = new AnnotatingBox(this, itemModel);
		add(annotatingBox);
		annotatingBox.selectedStatements.clear();
		add(new DownloadMetadataModal("downloadMetadataModal", this));
		uploadResourceModal = new UploadResourceModal("uploadResourceModal", this,
				((PortalApplication) getApplication()).getResourceGroups());
		add(uploadResourceModal);
		stmtEditForm = new StatementEditModal("statementEditModal", RoPage.this, new CompoundPropertyModel<Statement>(
				(Statement) null));
		add(stmtEditForm);
		relEditForm = new RelationEditModal("relationEditModal", RoPage.this, new CompoundPropertyModel<Statement>(
				(Statement) null), "loadingROFragment");
		add(relEditForm);

		add(new AbstractDefaultAjaxBehavior() {

			@Override
			protected void respond(AjaxRequestTarget target)
			{
				try {
					if (getConceptualResourcesTree() == null) {
						DateTime start = new DateTime();
						PortalApplication app = ((PortalApplication) getApplication());
						start = printDuration(start, "get app");
						Map<URI, Creator> usernames = MySession.get().getUsernames();
						start = printDuration(start, "get usernames");
						OntModel model = RoFactory.createManifestAndAnnotationsModel(roURI);
						start = printDuration(start, "create model");
						resources = RoFactory.getAggregatedResources(model, roURI, usernames);
						start = printDuration(start, "get aggregated res");
						RoFactory.assignResourceGroupsToResources(model, roURI, app.getResourceGroups(), resources);
						start = printDuration(start, "assign groups");
						setConceptualResourcesTree(RoFactory.createConceptualResourcesTree(model, roURI, resources));
						start = printDuration(start, "concept tree");
						setPhysicalResourcesTree(RoFactory.createPhysicalResourcesTree(model, roURI, resources,
							usernames));
						start = printDuration(start, "physical tree");

						RoFactory.createRelations(model, roURI, resources);
						start = printDuration(start, "relations");
						// FIXME this has been turned off because it takes too much time and is generally a hack
						//						RoFactory.createStabilities(model, roURI, resources);
						start = printDuration(start, "stabilities");

						//						itemModel.setObject((AggregatedResource) ((DefaultMutableTreeNode) getConceptualResourcesTree()
						//								.getRoot()).getUserObject());
						roViewerBox.onRoTreeLoaded();
						relEditForm.onRoTreeLoaded();
						target.add(roViewerBox);
						target.add(annotatingBox);
						target.add(relEditForm);
						start = printDuration(start, "update target");

						String json = RoFactory.createRoJSON(resources, interactiveViewColors);
						start = printDuration(start, "create json");
						String callback = roViewerBox.getInteractiveViewCallbackUrl().toString();
						target.appendJavaScript("var json = " + json + "; init(json, '" + callback + "');");
					}
				}
				catch (URISyntaxException | IOException e) {
					log.error(e);
				}
			}


			private DateTime printDuration(DateTime start, String comment)
			{
				DateTime end = new DateTime();
				log.debug("Duration " + new Duration(start, end).toString() + " (" + comment + ")");
				return end;
			}


			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);
				response.renderOnDomReadyJavaScript(getCallbackScript().toString());
			}

		});
	}


	/**
	 * @return the conceptualResourcesTree
	 * @throws URISyntaxException
	 */
	public RoTreeModel getConceptualResourcesTree()
		throws URISyntaxException
	{
		return conceptualResourcesTree;
	}


	/**
	 * @param conceptualResourcesTree
	 *            the conceptualResourcesTree to set
	 */
	public void setConceptualResourcesTree(RoTreeModel conceptualResourcesTree)
	{
		this.conceptualResourcesTree = conceptualResourcesTree;
	}


	public RoTreeModel getPhysicalResourcesTree()
	{
		return physicalResourcesTree;
	}


	public void setPhysicalResourcesTree(RoTreeModel physicalResourcesTree)
	{
		this.physicalResourcesTree = physicalResourcesTree;
	}

	@SuppressWarnings("serial")
	class ExternalLinkFragment
		extends Fragment
	{

		public ExternalLinkFragment(String id, String markupId, MarkupContainer markupProvider,
				CompoundPropertyModel<Statement> model, String property)
		{
			super(id, markupId, markupProvider, model);
			add(new ExternalLink("link", model.<String> bind(property), model.<String> bind(property)));
		}
	}

	@SuppressWarnings("serial")
	class InternalLinkFragment
		extends Fragment
	{

		public InternalLinkFragment(String id, String markupId, MarkupContainer markupProvider, Statement statement)
		{
			super(id, markupId, markupProvider);
			String internalName = "./" + roURI.relativize(statement.getSubjectURI()).toString();
			add(new AjaxLink<String>("link", new Model<String>(internalName)) {

				@Override
				public void onClick(AjaxRequestTarget target)
				{
					// TODO Auto-generated method stub

				}
			}.add(new Label("name", internalName.toString())));
		}
	}

	@SuppressWarnings("serial")
	class EditLinkFragment
		extends Fragment
	{

		public EditLinkFragment(String id, String markupId, MarkupContainer markupProvider,
				AjaxFallbackLink<String> link)
		{
			super(id, markupId, markupProvider);
			add(link);
		}
	}


	/**
	 * @param target
	 * @param uploadedFile
	 * @param selectedResourceGroups
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	void onResourceAdd(AjaxRequestTarget target, final FileUpload uploadedFile,
			Set<ResourceGroup> selectedResourceGroups)
		throws IOException, URISyntaxException
	{
		URI resourceURI = roURI.resolve(UrlEncoder.PATH_INSTANCE.encode(uploadedFile.getClientFileName(), "UTF-8"));
		ROSRService.sendResource(resourceURI, uploadedFile.getInputStream(), uploadedFile.getContentType(), MySession
				.get().getdLibraAccessToken());
		OntModel manifestModel = RoFactory.createManifestModel(roURI);
		Individual individual = manifestModel.createResource(resourceURI.toString()).as(Individual.class);
		for (ResourceGroup resourceGroup : selectedResourceGroups) {
			individual.addRDFType(manifestModel.createResource(resourceGroup.getRdfClasses().iterator().next()
					.toString()));
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		manifestModel.write(out);
		ROSRService.sendResource(roURI.resolve(".ro/manifest.rdf"), new ByteArrayInputStream(out.toByteArray()),
			"application/rdf+xml", MySession.get().getdLibraAccessToken());

		AggregatedResource resource = RoFactory
				.createResource(roURI, resourceURI, true, MySession.get().getUsernames());
		resource.getMatchingGroups().addAll(selectedResourceGroups);
		getConceptualResourcesTree().addAggregatedResource(resource, true);
		getPhysicalResourcesTree().addAggregatedResource(resource, false);
		roViewerBox.conceptualTree.invalidateAll();
		target.add(roViewerBox);

		resources.put(resourceURI, resource);
		RoFactory.addRelation(resources.get(roURI), RoFactory.aggregates, resource);
		String json = RoFactory.createRoJSON(resources, interactiveViewColors);
		String callback = roViewerBox.getInteractiveViewCallbackUrl().toString();
		target.appendJavaScript("var json = " + json + "; init(json, '" + callback + "');");
	}


	public void onResourceDelete(AggregatedResource resource, AjaxRequestTarget target)
		throws URISyntaxException, IOException
	{
		ROSRService.deleteResource(resource.getURI(), MySession.get().getdLibraAccessToken());
		getConceptualResourcesTree().removeAggregatedResource(resource);
		getPhysicalResourcesTree().removeAggregatedResource(resource);

		resources.remove(resource.getURI());
		for (Entry<String, AggregatedResource> entry : resource.getInverseRelations().entries()) {
			entry.getValue().getRelations().remove(entry.getKey(), resource);
		}
		String json = RoFactory.createRoJSON(resources, interactiveViewColors);
		String callback = roViewerBox.getInteractiveViewCallbackUrl().toString();
		target.appendJavaScript("var json = " + json + "; init(json, '" + callback + "');");
	}


	public void onResourceSelected(AjaxRequestTarget target)
	{
		annotatingBox.selectedStatements.clear();
		target.add(annotatingBox);
	}


	public void onRemoteResourceAdded(AjaxRequestTarget target, URI resourceURI, URI downloadURI,
			Set<ResourceGroup> selectedTypes)
		throws URISyntaxException, IOException
	{
		URI absoluteResourceURI = roURI.resolve(resourceURI);
		//		URI absoluteDownloadURI = (downloadURI != null ? roURI.resolve(downloadURI) : null);
		OntModel manifestModel = RoFactory.createManifestModel(roURI);
		// HACK this shouldn't be added by portal but rather by RODL
		Resource ro = manifestModel.createResource(roURI.toString());
		Individual individual = manifestModel.createResource(absoluteResourceURI.toString()).as(Individual.class);
		ro.addProperty(RoFactory.aggregates, individual);
		individual.addProperty(DCTerms.creator, manifestModel.createResource(MySession.get().getUserURI().toString()));
		individual.addRDFType(RoFactory.roResource);
		for (ResourceGroup resourceGroup : selectedTypes) {
			individual.addRDFType(manifestModel.createResource(resourceGroup.getRdfClasses().iterator().next()
					.toString()));
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		manifestModel.write(out);
		ROSRService.sendResource(roURI.resolve(".ro/manifest.rdf"), new ByteArrayInputStream(out.toByteArray()),
			"application/rdf+xml", MySession.get().getdLibraAccessToken());

		AggregatedResource resource = RoFactory.createResource(roURI, absoluteResourceURI, true, MySession.get()
				.getUsernames());
		resource.getMatchingGroups().addAll(selectedTypes);
		getConceptualResourcesTree().addAggregatedResource(resource, true);
		getPhysicalResourcesTree().addAggregatedResource(resource, false);
		roViewerBox.conceptualTree.invalidateAll();
		target.add(roViewerBox);

		resources.put(absoluteResourceURI, resource);
		RoFactory.addRelation(resources.get(roURI), RoFactory.aggregates, resource);
		String json = RoFactory.createRoJSON(resources, interactiveViewColors);
		String callback = roViewerBox.getInteractiveViewCallbackUrl().toString();
		target.appendJavaScript("var json = " + json + "; init(json, '" + callback + "');");
	}


	/**
	 * @param roPage
	 */
	void onMetadataDownload(RDFFormat format)
	{
		throw new RestartResponseException(RoPage.class, getPageParameters().add("redirectTo",
			roURI.resolve(".ro/manifest." + format.getDefaultFileExtension() + "?original=manifest.rdf").toString())
				.add("redirectDelay", 1));
	}


	/**
	 * @param roPage
	 * @throws URISyntaxException
	 */
	void onResourceDownload(URI downloadURI)
	{
		throw new RestartResponseException(RoPage.class, getPageParameters().add("redirectTo", downloadURI.toString())
				.add("redirectDelay", 1));
	}


	/**
	 * @param statement
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	void onStatementAdd(Statement statement)
		throws URISyntaxException, Exception
	{
		Token dLibraToken = MySession.get().getdLibraAccessToken();
		ClientResponse res = ROSRService.addAnnotation(roURI, statement.getSubjectURI(), MySession.get().getUserURI(),
			statement, dLibraToken);
		if (res.getStatus() != HttpServletResponse.SC_OK) {
			throw new Exception("Error when adding annotation: " + res.getClientResponseStatus());
		}
	}


	/**
	 * @param statement
	 * @throws Exception
	 */
	void onStatementEdit(Statement statement)
		throws Exception
	{
		Token dLibraToken = MySession.get().getdLibraAccessToken();
		Annotation ann = statement.getAnnotation();
		ClientResponse res = ROSRService.sendResource(ann.getBodyURI(), RoFactory.wrapAnnotationBody(ann.getBody()),
			"application/rdf+xml", dLibraToken);
		if (res.getStatus() != HttpServletResponse.SC_OK) {
			throw new Exception("Error when adding statement: " + res.getClientResponseStatus());
		}
	}


	/**
	 * @param target
	 * @param form
	 */
	void onStatementAddedEdited(AjaxRequestTarget target)
	{
		this.annotatingBox.getModelObject().setAnnotations(
			RoFactory.createAnnotations(roURI, this.annotatingBox.getModelObject().getURI(), MySession.get()
					.getUsernames()));
		target.add(roViewerBox.infoPanel);
		target.add(annotatingBox.annotationsDiv);
	}


	/**
	 * @param statement
	 * @param target
	 * @param form
	 */
	void onRelationAddedEdited(Statement statement, AjaxRequestTarget target)
	{
		this.annotatingBox.getModelObject().setAnnotations(
			RoFactory.createAnnotations(roURI, this.annotatingBox.getModelObject().getURI(), MySession.get()
					.getUsernames()));
		AggregatedResource subjectAR = resources.get(statement.getSubjectURI());
		AggregatedResource objectAR = resources.get(statement.getObjectURI());
		RoFactory.addRelation(subjectAR, statement.getPropertyLocalNameNice(), objectAR);
		target.add(roViewerBox.infoPanel);
		target.add(annotatingBox.annotationsDiv);
	}

}
