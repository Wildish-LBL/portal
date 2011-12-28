package pl.psnc.dl.wf4ever.portal.pages;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Annotation;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.pages.util.SelectableRefreshableView;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

public class RoPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private URI roURI;

	private boolean canEdit = false;

	private final RoViewerBox roViewerBox;

	private final AnnotatingBox annotatingBox;


	public RoPage(final PageParameters parameters)
		throws URISyntaxException, MalformedURLException, OAuthException
	{
		super(parameters);
		RoFactory factory = null;
		ResearchObject ro = null;
		if (!parameters.get("ro").isEmpty()) {
			roURI = new URI(UrlDecoder.QUERY_INSTANCE.decode(parameters.get("ro").toString(), "UTF-8"));
			factory = new RoFactory(roURI);
			ro = factory.createResearchObject(true);
		}
		else {
			throw new RestartResponseException(ErrorPage.class, new PageParameters().add("message",
				"The RO URI is missing"));
		}

		if (MySession.get().isSignedIn()) {
			List<URI> uris = ROSRService.getROList(MySession.get().getdLibraAccessToken());
			canEdit = uris.contains(ro.getURI());
		}

		final CompoundPropertyModel<AggregatedResource> itemModel = new CompoundPropertyModel<AggregatedResource>(ro);
		final TreeModel treeModel = factory.createAggregatedResourcesTree(ro, true);
		roViewerBox = new RoViewerBox(itemModel, treeModel);
		add(roViewerBox);
		annotatingBox = new AnnotatingBox(itemModel);
		add(annotatingBox);
		annotatingBox.setAnnotationSelection(ro.getAnnotations().isEmpty() ? null : ro.getAnnotations().get(0));
	}

	@SuppressWarnings("serial")
	class RoViewerBox
		extends WebMarkupContainer
	{

		final WebMarkupContainer itemInfo;


		public RoViewerBox(final CompoundPropertyModel<AggregatedResource> itemModel, TreeModel treeModel)
		{
			super("roViewerBox", itemModel);
			setOutputMarkupId(true);
			add(new Label("title", roURI.toString()));

			Form< ? > roForm = new Form<Void>("roForm");
			add(roForm);

			AjaxButton addFolder = new AjaxButton("addFolder", roForm) {

				private static final long serialVersionUID = -491963068167875L;


				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
				}


				@Override
				protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
				{
				}
			};
			addFolder.add(new Behavior() {

				@Override
				public void onComponentTag(Component component, ComponentTag tag)
				{
					super.onComponentTag(component, tag);
					if (!canEdit) {
						tag.append("class", "disabled", " ");
					}
				}
			});
			roForm.add(addFolder);

			AjaxButton addResource = new AjaxButton("addResource", roForm) {

				private static final long serialVersionUID = -491963068167875L;


				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
				}


				@Override
				protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
				{
				}
			};
			addResource.add(new Behavior() {

				@Override
				public void onComponentTag(Component component, ComponentTag tag)
				{
					super.onComponentTag(component, tag);
					if (!canEdit) {
						tag.append("class", "disabled", " ");
					}
				}
			});
			roForm.add(addResource);

			AjaxButton deleteResource = new AjaxButton("deleteResource", roForm) {

				private static final long serialVersionUID = -491963068167875L;


				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
				}


				@Override
				protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
				{
				}
			};
			deleteResource.add(new Behavior() {

				@Override
				public void onComponentTag(Component component, ComponentTag tag)
				{
					super.onComponentTag(component, tag);
					if (!canEdit) {
						tag.append("class", "disabled", " ");
					}
				}
			});
			roForm.add(deleteResource);

			itemInfo = new WebMarkupContainer("itemInfo", itemModel);
			itemInfo.setOutputMarkupId(true);
			add(itemInfo);
			itemInfo.add(new ExternalLink("resourceURI", itemModel.<String> bind("URI.toString"), itemModel
					.<URI> bind("URI")));
			itemInfo.add(new ExternalLink("downloadURI", itemModel.<String> bind("downloadURI.toString"), itemModel
					.<URI> bind("downloadURI")));
			itemInfo.add(new Label("creator"));
			itemInfo.add(new Label("createdFormatted"));
			itemInfo.add(new Label("sizeFormatted"));
			itemInfo.add(new Label("annotations.size"));

			Tree tree = new RoTree("treeTable", treeModel) {

				private static final long serialVersionUID = -7512570425701073804L;


				@Override
				protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node)
				{
					AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) node).getUserObject();
					itemModel.setObject(res);
					annotatingBox.setAnnotationSelection(res.getAnnotations().isEmpty() ? null : res.getAnnotations()
							.get(0));
					target.add(itemInfo);
					target.add(annotatingBox);
				}
			};
			tree.getTreeState().expandAll();
			tree.getTreeState().selectNode(treeModel.getRoot(), true);
			add(tree);
		}
	}

	@SuppressWarnings("serial")
	class AnnotatingBox
		extends WebMarkupContainer
	{

		final WebMarkupContainer entriesDiv;

		final WebMarkupContainer annotationsDiv;

		private final SelectableRefreshableView<Annotation> annList;


		public AnnotatingBox(final CompoundPropertyModel<AggregatedResource> itemModel)

		{
			super("annotatingBox", itemModel);
			setOutputMarkupId(true);
			add(new Label("annTarget", itemModel.<URI> bind("URI")));

			annotationsDiv = new WebMarkupContainer("annotationsDiv");
			annotationsDiv.setOutputMarkupId(true);
			add(annotationsDiv);
			annList = new SelectableRefreshableView<Annotation>("annsListView", new PropertyModel<List<Annotation>>(
					itemModel, "annotations")) {

				@Override
				protected void populateItem(Item<Annotation> item)
				{
					super.populateItem(item);
					item.add(new Label("createdFormatted"));
					item.add(new Label("creator"));
					item.add(new AttributeAppender("title", new PropertyModel<URI>(item.getModel(), "URI")));
					item.setOutputMarkupId(true);
				}


				@Override
				public void onSelectItem(AjaxRequestTarget target, Item<Annotation> item)
				{
					target.add(annotationsDiv);
					target.add(entriesDiv);
				}

			};
			annotationsDiv.add(annList);

			Form< ? > annForm = new Form<Void>("annForm");
			annotationsDiv.add(annForm);

			AjaxButton addAnnotation = new AjaxButton("addAnnotation", annForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					try {
						ROSRService.addAnnotation(roURI, itemModel.getObject().getURI(), "Yet unknown", MySession.get()
								.getdLibraAccessToken());
						RoFactory factory = new RoFactory(roURI);
						itemModel.getObject().setAnnotations(factory.createAnnotations(itemModel.getObject().getURI()));
						target.add(annotationsDiv);
						target.add(roViewerBox.itemInfo);
					}
					catch (OAuthException | URISyntaxException e) {
						error(e.getMessage());
					}
				}


				@Override
				protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
				{
				}
			};
			addAnnotation.add(new Behavior() {

				@Override
				public void onComponentTag(Component component, ComponentTag tag)
				{
					super.onComponentTag(component, tag);
					if (!canEdit) {
						tag.append("class", "disabled", " ");
					}
				}
			});
			annForm.add(addAnnotation);

			AjaxButton deleteAnnotation = new AjaxButton("deleteAnnotation", annForm) {

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
				{
					try {
						ROSRService.deleteAnnotation(roURI, annList.getSelectedObject().getURI(), MySession.get()
								.getdLibraAccessToken());
						RoFactory factory = new RoFactory(roURI);
						itemModel.getObject().setAnnotations(factory.createAnnotations(itemModel.getObject().getURI()));
						target.add(annotationsDiv);
						target.add(roViewerBox.itemInfo);
					}
					catch (OAuthException | URISyntaxException e) {
						error(e.getMessage());
					}
				}


				@Override
				protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
				{
				}
			};
			deleteAnnotation.add(new Behavior() {

				@Override
				public void onComponentTag(Component component, ComponentTag tag)
				{
					super.onComponentTag(component, tag);
					if (!canEdit || annList.getSelectedObject() == null) {
						tag.append("class", "disabled", " ");
					}
				}
			});
			annForm.add(deleteAnnotation);

			entriesDiv = new WebMarkupContainer("entriesDiv");
			entriesDiv.setOutputMarkupId(true);
			add(entriesDiv);
			SelectableRefreshableView<Statement> entriesList = new SelectableRefreshableView<Statement>(
					"entriesListView", new PropertyModel<List<Statement>>(annList, "selectedObject.body")) {

				private static final long serialVersionUID = -6310254217773728128L;


				@Override
				protected void populateItem(Item<Statement> item)
				{
					super.populateItem(item);
					item.add(new Label("propertyLocalName"));
					if (item.getModelObject().isObjectURIResource()) {
						item.add(new ExternalLinkFragment("object", "externalLinkFragment", RoPage.this,
								(CompoundPropertyModel<Statement>) item.getModel()));
					}
					else {
						item.add(new Label("object", ((CompoundPropertyModel<Statement>) item.getModel())
								.<String> bind("objectValue")).setEscapeModelStrings(false));
					}
				}


				@Override
				public void onSelectItem(AjaxRequestTarget target, Item<Statement> item)
				{
					target.add(entriesDiv);
				}

			};
			entriesDiv.add(entriesList);
		}


		public void setAnnotationSelection(Annotation ann)
		{
			annList.setSelectedObject(ann);

		}
	}

	class ExternalLinkFragment
		extends Fragment
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7541060078430742169L;


		public ExternalLinkFragment(String id, String markupId, MarkupContainer markupProvider,
				CompoundPropertyModel<Statement> model)
		{
			super(id, markupId, markupProvider, model);
			add(new ExternalLink("link", model.<String> bind("objectURI"), model.<String> bind("objectValue")));
		}
	}

}
