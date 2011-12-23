package pl.psnc.dl.wf4ever.portal.pages;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Annotation;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

import com.hp.hpl.jena.rdf.model.Statement;

public class RoPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private AggregatedResource ro;

	private boolean canEdit = false;

	private final WebMarkupContainer roViewerBox;

	private final WebMarkupContainer annotatingBox;


	public RoPage(final PageParameters parameters)
		throws URISyntaxException, MalformedURLException, OAuthException
	{
		super(parameters);
		RoFactory factory = null;
		if (!parameters.get("ro").isEmpty()) {
			URI roURI = new URI(UrlDecoder.QUERY_INSTANCE.decode(parameters.get("ro").toString(), "UTF-8"));
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
		annotatingBox = createAnnotatingBox(itemModel);
		add(annotatingBox);
		roViewerBox = createRoViewerBox(itemModel, treeModel);
		add(roViewerBox);
	}


	private WebMarkupContainer createRoViewerBox(final CompoundPropertyModel<AggregatedResource> itemModel,
			TreeModel treeModel)
	{
		WebMarkupContainer box = new WebMarkupContainer("roViewerBox", itemModel);
		box.add(new Label("title", ro.getURI().toString()));

		Form< ? > roForm = new Form<Void>("roForm");
		box.add(roForm);

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
		if (!canEdit) {
			addFolder.add(new AttributeAppender("class", " disabled"));
		}
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
		if (!canEdit) {
			addResource.add(new AttributeAppender("class", " disabled"));
		}
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
		if (!canEdit) {
			deleteResource.add(new AttributeAppender("class", " disabled"));
		}
		roForm.add(deleteResource);

		final WebMarkupContainer itemInfo = new WebMarkupContainer("itemInfo", itemModel);
		itemInfo.setOutputMarkupId(true);
		box.add(itemInfo);
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
				target.add(itemInfo);
				target.add(annotatingBox);
			}
		};
		tree.getTreeState().expandAll();
		tree.getTreeState().selectNode(treeModel.getRoot(), true);
		box.add(tree);
		return box;
	}


	private WebMarkupContainer createAnnotatingBox(final CompoundPropertyModel<AggregatedResource> itemModel)
	{
		WebMarkupContainer box = new WebMarkupContainer("annotatingBox", itemModel);
		box.setOutputMarkupId(true);
		box.add(new Label("annTarget", itemModel.<URI> bind("URI")));

		final WebMarkupContainer entriesDiv = new WebMarkupContainer("entriesDiv");
		entriesDiv.setOutputMarkupId(true);
		box.add(entriesDiv);
		final PropertyListView<Statement> entriesList = new PropertyListView<Statement>("entriesListView") {

			private static final long serialVersionUID = -6310254217773728128L;


			@Override
			protected void populateItem(ListItem<Statement> item)
			{
				item.add(new Label("predicate.localName"));
				if (item.getModelObject().getObject().isResource()) {
					item.add(new ExternalLink("object", ((CompoundPropertyModel<Statement>) item.getModel())
							.<String> bind("object.asResource.URI"), ((CompoundPropertyModel<Statement>) item
							.getModel()).<String> bind("object.asResource.toString")));
				}
				else {
					item.add(new Label("object", ((CompoundPropertyModel<Statement>) item.getModel())
							.<String> bind("object.asLiteral.value")).setEscapeModelStrings(false));
				}
			}

		};
		entriesList.setReuseItems(false);
		entriesDiv.add(entriesList);

		final WebMarkupContainer annotationsListDiv = new WebMarkupContainer("annotationsDiv");
		annotationsListDiv.setOutputMarkupId(true);
		box.add(annotationsListDiv);
		SelectablePropertyListView<Annotation> annList = new SelectablePropertyListView<Annotation>("annsListView",
				new PropertyModel<List<Annotation>>(itemModel, "annotations")) {

			private static final long serialVersionUID = -6310254217773728128L;


			@Override
			protected void populateItem(ListItem<Annotation> item)
			{
				super.populateItem(item);
				item.add(new Label("createdFormatted"));
				item.add(new Label("creator"));
				item.add(new AttributeAppender("title", new PropertyModel<URI>(item.getModel(), "URI")));
			}


			@Override
			public void onDeselectObject(AjaxRequestTarget target, ListItem<Annotation> item)
			{
				// TODO Auto-generated method stub

			}


			@Override
			public void onSelectObject(AjaxRequestTarget target, ListItem<Annotation> item)
			{
				target.add(entriesDiv);
			}

		};
		annList.setReuseItems(true);
		annotationsListDiv.add(annList);

		entriesList.setModel(new PropertyModel<List<Statement>>(annList, "selectedObject.body"));

		return box;
	}
}
