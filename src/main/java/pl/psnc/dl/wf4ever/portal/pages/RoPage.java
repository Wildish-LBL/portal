package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
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
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.lang.Bytes;
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
import pl.psnc.dl.wf4ever.portal.pages.util.URIConverter;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

import com.sun.jersey.api.client.ClientResponse;

public class RoPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private URI roURI;

	private boolean canEdit = false;

	private final RoViewerBox roViewerBox;

	private final AnnotatingBox annotatingBox;

	private RoTreeModel aggregatedResourcesTree;


	public RoPage(final PageParameters parameters)
		throws URISyntaxException, MalformedURLException, OAuthException
	{
		super(parameters);
		if (!parameters.get("ro").isEmpty()) {
			PortalApplication app = ((PortalApplication) getApplication());
			roURI = new URI(UrlDecoder.QUERY_INSTANCE.decode(parameters.get("ro").toString(), "UTF-8"));
			setAggregatedResourcesTree(RoFactory.createAggregatedResourcesTree(roURI, app.getResourceGroups(),
				app.getResourceGroupDescriptions()));
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
				(AggregatedResource) ((DefaultMutableTreeNode) getAggregatedResourcesTree().getRoot()).getUserObject());
		roViewerBox = new RoViewerBox(itemModel, new PropertyModel<TreeModel>(this, "aggregatedResourcesTree"));
		add(roViewerBox);
		annotatingBox = new AnnotatingBox(itemModel);
		add(annotatingBox);
		annotatingBox.selectedStatements.clear();
		add(new ChooseRdfFormat());
		add(new UploadResourceForm());
	}


	/**
	 * @return the aggregatedResourcesTree
	 * @throws URISyntaxException
	 */
	public RoTreeModel getAggregatedResourcesTree()
		throws URISyntaxException
	{
		if (aggregatedResourcesTree == null) {
			PortalApplication app = ((PortalApplication) getApplication());
			aggregatedResourcesTree = RoFactory.createAggregatedResourcesTree(roURI, app.getResourceGroups(),
				app.getResourceGroupDescriptions());
		}
		return aggregatedResourcesTree;
	}


	/**
	 * @param aggregatedResourcesTree
	 *            the aggregatedResourcesTree to set
	 */
	public void setAggregatedResourcesTree(RoTreeModel aggregatedResourcesTree)
	{
		this.aggregatedResourcesTree = aggregatedResourcesTree;
	}

	@SuppressWarnings("serial")
	class RoViewerBox
		extends WebMarkupContainer
	{

		RoTree tree;

		Panel infoPanel;

		private final ItemInfoPanel itemInfo;

		private final InfoPanel resourceGroupInfo;

		final WebMarkupContainer actionButtons;


		public RoViewerBox(final CompoundPropertyModel<AggregatedResource> itemModel,
				IModel< ? extends TreeModel> treeModel)
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

			tree = new RoTree("treeTable", treeModel) {

				private static final long serialVersionUID = -7512570425701073804L;


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
			tree.getTreeState().collapseAll();
			tree.getTreeState().expandNode(treeModel.getObject().getRoot());
			tree.getTreeState().selectNode(treeModel.getObject().getRoot(), true);
			add(tree);

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
					AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) tree.getTreeState()
							.getSelectedNodes().iterator().next()).getUserObject();
					try {
						ROSRService.deleteResource(res.getURI(), MySession.get().getdLibraAccessToken());
						getAggregatedResourcesTree().removeAggregatedResource(res);
						tree.invalidateAll();
						target.add(RoViewerBox.this);
					}
					catch (Exception e) {
						error(e);
					}
				}


				@Override
				public boolean isEnabled()
				{
					if (super.isEnabled() && canEdit && !tree.getTreeState().getSelectedNodes().isEmpty()) {
						Object object = ((DefaultMutableTreeNode) tree.getTreeState().getSelectedNodes().iterator()
								.next()).getUserObject();
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
			roForm.add(downloadMetadata);
		}
	}

	@SuppressWarnings("serial")
	class AnnotatingBox
		extends WebMarkupContainer
	{

		final WebMarkupContainer annotationsDiv;

		final PropertyListView<Annotation> annList;

		final List<Statement> selectedStatements = new ArrayList<Statement>();

		private final StatementEditForm stmtEditForm;


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
								if (statement.isSubjectPartOfRo(roURI)) {
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
						stmtEditForm.setTitle("Add statement");
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
						RoFactory.createAnnotations(roURI, AnnotatingBox.this.getModelObject().getURI()));
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

			stmtEditForm = new StatementEditForm(new CompoundPropertyModel<Statement>((Statement) null));
			add(stmtEditForm);
		}

		class StatementEditForm
			extends Form<Statement>
		{

			private final TextArea<String> value;

			private final TextField<URI> objectURI;

			private final TextField<URI> propertyURI;

			private URI selectedProperty;

			private String title;


			public StatementEditForm(CompoundPropertyModel<Statement> model)
			{
				super("stmtEditForm", model);

				add(new Label("title", new PropertyModel<String>(this, "title")));

				List<URI> choices = Arrays.asList(RoFactory.defaultProperties);
				DropDownChoice<URI> properties = new DropDownChoice<URI>("propertyURI", new PropertyModel<URI>(this,
						"selectedProperty"), choices);
				properties.setNullValid(true);
				add(properties);

				final WebMarkupContainer propertyURIDiv = new WebMarkupContainer("customPropertyURIDiv");
				add(propertyURIDiv);

				propertyURI = new TextField<URI>("customPropertyURI", new PropertyModel<URI>(this, "customProperty"),
						URI.class) {

					@SuppressWarnings("unchecked")
					@Override
					public <C> IConverter<C> getConverter(Class<C> type)
					{
						return (IConverter<C>) new URIConverter();
					}
				};
				propertyURIDiv.add(propertyURI);

				final WebMarkupContainer uriDiv = new WebMarkupContainer("objectURIDiv");
				add(uriDiv);

				objectURI = new TextField<URI>("objectURI", URI.class) {

					@SuppressWarnings("unchecked")
					@Override
					public <C> IConverter<C> getConverter(Class<C> type)
					{
						return (IConverter<C>) new URIConverter();
					}
				};
				uriDiv.add(objectURI);

				final WebMarkupContainer valueDiv = new WebMarkupContainer("objectValueDiv");
				add(valueDiv);

				value = new TextArea<String>("objectValue");
				value.setEscapeModelStrings(false);
				valueDiv.add(value);

				CheckBox objectType = new CheckBox("objectURIResource");
				add(objectType);

				add(new MyAjaxButton("save", this) {

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
					{
						super.onSubmit(target, form);
						Token dLibraToken = getSession().getdLibraAccessToken();
						Statement statement = StatementEditForm.this.getModelObject();
						try {
							if (statement.getAnnotation() == null) {
								ClientResponse res = ROSRService.addAnnotation(roURI, statement.getSubjectURI(),
									getSession().getUsername("Unknown"), statement, dLibraToken);
								if (res.getStatus() != HttpServletResponse.SC_OK) {
									throw new Exception("Error when adding annotation: "
											+ res.getClientResponseStatus());
								}
							}
							else {
								Annotation ann = statement.getAnnotation();
								ClientResponse res = ROSRService.sendResource(ann.getBodyURI(),
									RoFactory.wrapAnnotationBody(ann.getBody()), "application/rdf+xml", dLibraToken);
								if (res.getStatus() != HttpServletResponse.SC_OK) {
									throw new Exception("Error when adding statement: " + res.getClientResponseStatus());
								}
							}
							//							roFactory.reload();
							AnnotatingBox.this.getModelObject().setAnnotations(
								RoFactory.createAnnotations(roURI, AnnotatingBox.this.getModelObject().getURI()));
							target.add(form);
							target.add(roViewerBox.infoPanel);
							target.add(annotatingBox.annotationsDiv);
							target.appendJavaScript("$('#edit-ann-modal').modal('hide')");
						}
						catch (Exception e) {
							error("" + e.getMessage());
						}
					}
				});
				add(new MyAjaxButton("cancel", this) {

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
					{
						super.onSubmit(target, form);
						target.appendJavaScript("$('#edit-ann-modal').modal('hide')");
					}
				}.setDefaultFormProcessing(false));
			}


			/**
			 * @return the selectedProperty
			 */
			public URI getSelectedProperty()
			{
				if (selectedProperty == null && getModelObject() != null)
					return getModelObject().getPropertyURI();
				return selectedProperty;
			}


			/**
			 * @param selectedProperty
			 *            the selectedProperty to set
			 */
			public void setSelectedProperty(URI selectedProperty)
			{
				this.selectedProperty = selectedProperty;
				if (selectedProperty != null)
					getModelObject().setPropertyURI(selectedProperty);
			}


			/**
			 * @return the selectedProperty
			 */
			public URI getCustomProperty()
			{
				if (getModelObject() != null)
					return getModelObject().getPropertyURI();
				return null;
			}


			/**
			 * @param selectedProperty
			 *            the selectedProperty to set
			 */
			public void setCustomProperty(URI customProperty)
			{
				if (selectedProperty == null && customProperty != null)
					getModelObject().setPropertyURI(customProperty);
			}


			/**
			 * @return the title
			 */
			public String getTitle()
			{
				return title;
			}


			/**
			 * @param title
			 *            the title to set
			 */
			public void setTitle(String title)
			{
				this.title = title;
			}
		}
	}

	@SuppressWarnings("serial")
	class ChooseRdfFormat
		extends Form<Void>
	{

		private RDFFormat format = RDFFormat.RDFXML;


		public ChooseRdfFormat()
		{
			super("downloadMetadataForm");
			List<RDFFormat> formats = Arrays.asList(RDFFormat.RDFXML, RDFFormat.TURTLE, RDFFormat.TRIG, RDFFormat.TRIX,
				RDFFormat.N3);
			DropDownChoice<RDFFormat> formatDropDown = new DropDownChoice<RDFFormat>("rdfFormat",
					new PropertyModel<RDFFormat>(this, "format"), formats, new IChoiceRenderer<RDFFormat>() {

						@Override
						public Object getDisplayValue(RDFFormat format)
						{
							return format.getName();
						}


						@Override
						public String getIdValue(RDFFormat format, int index)
						{
							return "" + index;
						}
					});
			add(formatDropDown);
			add(new MyAjaxButton("confirmDownloadMetadata", this) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					throw new RestartResponseException(RoPage.class, RoPage.this
							.getPageParameters()
							.add("redirectTo",
								roURI.resolve(".ro/manifest." + getFormat().getDefaultFileExtension()).toString())
							.add("redirectDelay", 1));
				}
			});
			add(new MyAjaxButton("cancelDownloadMetadata", this) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					target.appendJavaScript("$('#download-metadata-modal').modal('hide')");
				}
			}.setDefaultFormProcessing(false));
		}


		public RDFFormat getFormat()
		{
			return format;
		}


		public void setFormat(RDFFormat format)
		{
			this.format = format;
		}
	}

	@SuppressWarnings("serial")
	class UploadResourceForm
		extends Form<Void>
	{

		public UploadResourceForm()
		{
			super("uploadResourceForm");

			// Enable multipart mode (need for uploads file)
			setMultiPart(true);

			// max upload size, 10k
			setMaxSize(Bytes.megabytes(10));
			final FileUploadField fileUpload = new FileUploadField("fileUpload");
			add(fileUpload);
			add(new MyAjaxButton("confirmUploadResource", this) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					final FileUpload uploadedFile = fileUpload.getFileUpload();
					if (uploadedFile != null) {
						URI resourceURI = roURI.resolve(UrlEncoder.PATH_INSTANCE.encode(
							uploadedFile.getClientFileName(), "UTF-8"));
						try {
							ROSRService.sendResource(resourceURI, uploadedFile.getInputStream(),
								uploadedFile.getContentType(), MySession.get().getdLibraAccessToken());
							AggregatedResource resource = RoFactory.createResource(roURI, resourceURI, true);
							getAggregatedResourcesTree().addAggregatedResource(resource);
							roViewerBox.tree.invalidateAll();
							target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
							target.add(roViewerBox);
						}
						catch (IOException | URISyntaxException e) {
							error(e);
						}
					}
				}
			});
			add(new MyAjaxButton("cancelUploadResource", this) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					super.onSubmit(target, form);
					target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
				}
			}.setDefaultFormProcessing(false));
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

}
