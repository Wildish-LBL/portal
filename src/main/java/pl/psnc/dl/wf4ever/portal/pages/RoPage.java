package pl.psnc.dl.wf4ever.portal.pages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Annotation;
import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.RoTree;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.sun.jersey.api.client.ClientResponse;

public class RoPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(RoPage.class);

	URI roURI;

	private Map<URI, AggregatedResource> resources;

	private boolean canEdit = false;

	final RoViewerBox roViewerBox;

	final AnnotatingBox annotatingBox;

	private RoTreeModel conceptualResourcesTree;

	private RoTreeModel physicalResourcesTree;

	private final StatementEditModal stmtEditForm;

	private final RelationEditModal relEditForm;


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
		roViewerBox = new RoViewerBox(itemModel, new PropertyModel<TreeModel>(this, "conceptualResourcesTree"),
				new PropertyModel<TreeModel>(this, "physicalResourcesTree"), "loadingROFragment");
		add(roViewerBox);
		annotatingBox = new AnnotatingBox(itemModel);
		add(annotatingBox);
		annotatingBox.selectedStatements.clear();
		add(new DownloadMetadataModal("downloadMetadataModal", this));
		add(new UploadResourceModal("uploadResourceModal", this,
				((PortalApplication) getApplication()).getResourceGroups()));
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
						PortalApplication app = ((PortalApplication) getApplication());
						Map<URI, String> usernames = MySession.get().getUsernames();
						OntModel model = RoFactory.createManifestAndAnnotationsModel(roURI);
						resources = RoFactory.getAggregatedResources(model, roURI, usernames);
						setConceptualResourcesTree(RoFactory.createConceptualResourcesTree(model, roURI,
							app.getResourceGroups(), resources, usernames));
						setPhysicalResourcesTree(RoFactory.createPhysicalResourcesTree(model, roURI, resources,
							usernames));

						RoFactory.createRelations(model, roURI, resources);
						RoFactory.createStabilities(model, roURI, resources);

						itemModel.setObject((AggregatedResource) ((DefaultMutableTreeNode) getConceptualResourcesTree()
								.getRoot()).getUserObject());
						roViewerBox.onRoTreeLoaded();
						relEditForm.onRoTreeLoaded();
						target.add(roViewerBox);
						target.add(annotatingBox);
						target.add(relEditForm);
					}
				}
				catch (URISyntaxException e) {
					log.error(e);
				}
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
	class RoViewerBox
		extends WebMarkupContainer
	{

		RoTree conceptualTree;

		RoTree physicalTree;

		Panel infoPanel;

		private final ItemInfoPanel itemInfo;

		private final InfoPanel resourceGroupInfo;

		final WebMarkupContainer actionButtons;

		private Fragment conceptualTreeLoading;

		private Fragment physicalTreeLoading;


		public RoViewerBox(final CompoundPropertyModel<AggregatedResource> itemModel,
				IModel< ? extends TreeModel> conceptualTreeModel, PropertyModel<TreeModel> physicalTreeModel,
				String tempRoTreeId)
		{
			super("roViewerBox", itemModel);
			setOutputMarkupId(true);
			add(new Label("title", roURI.toString()));

			final CompoundPropertyModel<ResourceGroup> resourceGroupModel = new CompoundPropertyModel<ResourceGroup>(
					(ResourceGroup) null);
			itemInfo = new ItemInfoPanel("itemInfo", itemModel);
			resourceGroupInfo = new InfoPanel("itemInfo", resourceGroupModel);
			infoPanel = itemInfo;
			add(infoPanel);

			conceptualTreeLoading = new Fragment("treeTable", tempRoTreeId, RoPage.this);
			conceptualTree = new RoTree("treeTable", conceptualTreeModel) {

				@Override
				protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node)
				{
					Object object = ((DefaultMutableTreeNode) node).getUserObject();
					if (object instanceof AggregatedResource) {
						AggregatedResource res = (AggregatedResource) object;
						itemModel.setObject(res);
						if (infoPanel != itemInfo) {
							infoPanel.replaceWith(itemInfo);
							infoPanel = itemInfo;
						}
					}
					else if (object instanceof ResourceGroup) {
						ResourceGroup res = (ResourceGroup) object;
						resourceGroupModel.setObject(res);
						if (infoPanel != resourceGroupInfo) {
							infoPanel.replaceWith(resourceGroupInfo);
							infoPanel = resourceGroupInfo;
						}
					}
					annotatingBox.selectedStatements.clear();
					target.add(actionButtons);
					target.add(infoPanel);
					target.add(annotatingBox);
				}
			};
			add(conceptualTreeLoading);

			physicalTreeLoading = new Fragment("physicalTreeTable", tempRoTreeId, RoPage.this);
			physicalTree = new RoTree("physicalTreeTable", physicalTreeModel) {

				@Override
				protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node)
				{
					Object object = ((DefaultMutableTreeNode) node).getUserObject();
					if (object instanceof AggregatedResource) {
						AggregatedResource res = (AggregatedResource) object;
						itemModel.setObject(res);
						if (infoPanel != itemInfo) {
							infoPanel.replaceWith(itemInfo);
							infoPanel = itemInfo;
						}
					}
					annotatingBox.selectedStatements.clear();
					target.add(actionButtons);
					target.add(infoPanel);
					target.add(annotatingBox);
				}
			};
			add(physicalTreeLoading);

			Form< ? > roForm = new Form<Void>("roForm");
			add(roForm);

			actionButtons = new WebMarkupContainer("actionButtons");
			actionButtons.setOutputMarkupId(true);
			roForm.add(actionButtons);

			MyAjaxButton addResource = new MyAjaxButton("addResource", roForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					target.appendJavaScript("$('#upload-resource-modal').modal('show')");
				}


				@Override
				public boolean isEnabled()
				{
					return super.isEnabled() && canEdit;
				}
			};
			actionButtons.add(addResource);

			MyAjaxButton deleteResource = new MyAjaxButton("deleteResource", roForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) conceptualTree
							.getTreeState().getSelectedNodes().iterator().next()).getUserObject();
					try {
						ROSRService.deleteResource(res.getURI(), MySession.get().getdLibraAccessToken());
						getConceptualResourcesTree().removeAggregatedResource(res);
						conceptualTree.invalidateAll();
						target.add(RoViewerBox.this);
					}
					catch (Exception e) {
						error(e);
					}
				}


				@Override
				public boolean isEnabled()
				{
					if (super.isEnabled() && canEdit && !conceptualTree.getTreeState().getSelectedNodes().isEmpty()) {
						Object object = ((DefaultMutableTreeNode) conceptualTree.getTreeState().getSelectedNodes()
								.iterator().next()).getUserObject();
						return object instanceof AggregatedResource
								&& !((AggregatedResource) object).getURI().equals(roURI);
					}
					return false;
				}
			};
			actionButtons.add(deleteResource);

			AjaxButton downloadMetadata = new MyAjaxButton("downloadMetadata", roForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					target.appendJavaScript("$('#download-metadata-modal').modal('show')");
				}

			};
			actionButtons.add(downloadMetadata);
		}


		public void onRoTreeLoaded()
		{
			conceptualTreeLoading.replaceWith(conceptualTree);
			physicalTreeLoading.replaceWith(physicalTree);
		}
	}

	@SuppressWarnings("serial")
	class AnnotatingBox
		extends WebMarkupContainer
	{

		final WebMarkupContainer annotationsDiv;

		final PropertyListView<Annotation> annList;

		final List<Statement> selectedStatements = new ArrayList<Statement>();


		public AggregatedResource getModelObject()
		{
			return (AggregatedResource) getDefaultModelObject();
		}


		public AnnotatingBox(final CompoundPropertyModel<AggregatedResource> itemModel)

		{
			super("annotatingBox", itemModel);
			setOutputMarkupId(true);
			add(new Label("annTarget", itemModel.<URI> bind("URI")));

			annotationsDiv = new WebMarkupContainer("annotationsDiv");
			annotationsDiv.setOutputMarkupId(true);
			add(annotationsDiv);

			Form< ? > annForm = new Form<Void>("annotationsForm");
			annotationsDiv.add(annForm);
			CheckGroup<Statement> group = new CheckGroup<Statement>("group", selectedStatements);
			annForm.add(group);

			annList = new PropertyListView<Annotation>("annotationsList", new PropertyModel<List<Annotation>>(
					itemModel, "annotations")) {

				@Override
				protected void populateItem(ListItem<Annotation> item)
				{
					final Annotation annotation = item.getModelObject();
					item.add(new AttributeAppender("title", new PropertyModel<URI>(annotation, "URI")));
					PropertyListView<Statement> statementsList = new PropertyListView<Statement>("statementsList",
							new PropertyModel<List<Statement>>(annotation, "body")) {

						@Override
						protected void populateItem(final ListItem<Statement> item)
						{
							final Statement statement = item.getModelObject();
							item.add(new Check<Statement>("checkbox", item.getModel()));
							if (statement.isSubjectURIResource()) {
								if (RoFactory.isResourceInternal(roURI, statement.getSubjectURI())) {
									if (statement.getSubjectURI().equals(itemModel.getObject().getURI())) {
										item.add(new Label("subject", "[This item]"));
									}
									else {
										item.add(new InternalLinkFragment("subject", "internalLinkFragment",
												RoPage.this, statement));
									}
								}
								else {
									item.add(new ExternalLinkFragment("subject", "externalLinkFragment", RoPage.this,
											(CompoundPropertyModel<Statement>) item.getModel(), "subjectURI"));
								}
							}
							else {
								item.add(new Label("subject", ((CompoundPropertyModel<Statement>) item.getModel())
										.<String> bind("subjectValue")).setEscapeModelStrings(false));
							}
							item.add(new Label("propertyLocalNameNice"));
							if (statement.isObjectURIResource()) {
								item.add(new ExternalLinkFragment("object", "externalLinkFragment", RoPage.this,
										(CompoundPropertyModel<Statement>) item.getModel(), "objectURI"));
							}
							else {
								item.add(new Label("object", ((CompoundPropertyModel<Statement>) item.getModel())
										.<String> bind("objectValue")).setEscapeModelStrings(false));
							}
							if (canEdit) {
								item.add(new EditLinkFragment("edit", "editLinkFragment", RoPage.this,
										new AjaxFallbackLink<String>("link") {

											@Override
											public void onClick(AjaxRequestTarget target)
											{
												stmtEditForm.setModelObject(statement);
												stmtEditForm.setTitle("Edit statement");
												target.add(stmtEditForm);
												target.appendJavaScript("showStmtEdit('"
														+ StringEscapeUtils.escapeEcmaScript(statement.getObjectValue())
														+ "');");
											}
										}));
							}
							else {
								item.add(new Label("edit", "Edit"));
							}
						}
					};
					item.add(statementsList);
					item.add(new Label("creator"));
					item.add(new Label("createdAgoFormatted"));
				}
			};
			group.add(annList);

			AjaxButton addStatement = new MyAjaxButton("addAnnotation", annForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					try {
						stmtEditForm.setModelObject(new Statement(itemModel.getObject().getURI(), null));
						stmtEditForm.setTitle("Add annotation");
						target.add(stmtEditForm);
						target.appendJavaScript("showStmtEdit('');");
					}
					catch (Exception e) {
						error(e.getMessage());
					}
				}


				@Override
				public boolean isEnabled()
				{
					return super.isEnabled() && canEdit;
				}
			};
			annForm.add(addStatement);

			AjaxButton deleteStatement = new MyAjaxButton("deleteAnnotation", annForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					Token dLibraToken = getSession().getdLibraAccessToken();
					List<Annotation> annotations = new ArrayList<Annotation>();
					for (Statement statement : selectedStatements) {
						statement.getAnnotation().getBody().remove(statement);
						annotations.add(statement.getAnnotation());
						AggregatedResource subjectAR = resources.get(statement.getSubjectURI());
						AggregatedResource objectAR = resources.get(statement.getObjectURI());
						if (subjectAR != null && objectAR != null) {
							subjectAR.getRelations().remove(statement.getPropertyLocalNameNice(), objectAR);
						}
					}
					for (Annotation annotation : annotations) {
						try {
							if (annotation.getBody().isEmpty()) {
								ROSRService.deleteResource(annotation.getBodyURI(), dLibraToken);
								ROSRService.deleteAnnotation(roURI, annotation.getURI(), dLibraToken);
							}
							else {
								ROSRService.sendResource(annotation.getBodyURI(),
									RoFactory.wrapAnnotationBody(annotation.getBody()), "application/rdf+xml",
									dLibraToken);
							}
						}
						catch (Exception e) {
							error(e);
						}
					}
					//					roFactory.reload();
					AnnotatingBox.this.getModelObject().setAnnotations(
						RoFactory.createAnnotations(roURI, AnnotatingBox.this.getModelObject().getURI(), MySession
								.get().getUsernames()));
					selectedStatements.clear();
					target.add(annotatingBox.annotationsDiv);
					target.add(roViewerBox.infoPanel);
				}


				@Override
				public boolean isEnabled()
				{
					return super.isEnabled() && canEdit /*
														 * &&
														 * !selectedStatements.isEmpty()
														 */;
				}
			};
			annForm.add(deleteStatement);

			AjaxButton addRelation = new MyAjaxButton("addRelation", annForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					try {
						relEditForm.setModelObject(new Statement(itemModel.getObject().getURI(), null));
						relEditForm.setTitle("Add relation");
						target.add(relEditForm);
						target.appendJavaScript("showRelEdit();");
					}
					catch (Exception e) {
						error(e.getMessage());
					}
				}


				@Override
				public boolean isEnabled()
				{
					return super.isEnabled() && canEdit;
				}
			};
			annForm.add(addRelation);
		}
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
	void onFileUploaded(AjaxRequestTarget target, final FileUpload uploadedFile,
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
		getConceptualResourcesTree().addAggregatedResource(resource, selectedResourceGroups);
		roViewerBox.conceptualTree.invalidateAll();
		target.add(roViewerBox);
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
		subjectAR.getRelations().put(statement.getPropertyLocalNameNice(), objectAR);
		target.add(roViewerBox.infoPanel);
		target.add(annotatingBox.annotationsDiv);
	}
}
