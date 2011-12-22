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

public class RoPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private AggregatedResource ro;

	private boolean canEdit = false;


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

		add(new Label("title", ro.getURI().toString()));

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

		final CompoundPropertyModel<AggregatedResource> itemModel = new CompoundPropertyModel<AggregatedResource>(ro);
		final WebMarkupContainer itemInfo = new WebMarkupContainer("itemInfo", itemModel);
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

		final WebMarkupContainer annotationsListDiv = new WebMarkupContainer("annotationsList");
		annotationsListDiv.setOutputMarkupId(true);
		add(annotationsListDiv);
		PropertyListView<Annotation> annList = new PropertyListView<Annotation>("annsListView",
				new PropertyModel<List<Annotation>>(itemModel, "annotations")) {

			private static final long serialVersionUID = -6310254217773728128L;


			@Override
			protected void populateItem(ListItem<Annotation> item)
			{
				item.add(new Label("createdFormatted"));
				item.add(new Label("creator"));
			}

		};
		annList.setReuseItems(true);
		annList.setOutputMarkupId(true);
		annotationsListDiv.add(annList);

		TreeModel treeModel = factory.createAggregatedResourcesTree(ro, true);
		Tree tree = new RoTree("treeTable", treeModel) {

			private static final long serialVersionUID = -7512570425701073804L;


			@Override
			protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node)
			{
				AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) node).getUserObject();
				itemModel.setObject(res);
				target.add(itemInfo);
				target.add(annotationsListDiv);
			}
		};
		tree.getTreeState().expandAll();
		tree.getTreeState().selectNode(treeModel.getRoot(), true);
		add(tree);
	}
}
