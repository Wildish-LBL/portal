package pl.psnc.dl.wf4ever.portal.pages;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

public class RoPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private ResearchObject ro;

	private boolean canEdit = false;


	public RoPage(final PageParameters parameters)
		throws URISyntaxException, MalformedURLException, OAuthException
	{
		super(parameters);
		if (!parameters.get("ro").isEmpty()) {
			URI roURI = new URI(UrlDecoder.QUERY_INSTANCE.decode(parameters.get("ro").toString(), "UTF-8"));
			ro = new ResearchObject(roURI);
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

		Tree tree = new RoTree("treeTable", ro.getAggregatedResourcesTree());
		tree.getTreeState().expandAll();
		add(tree);

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

		CompoundPropertyModel<AggregatedResource> itemModel = new CompoundPropertyModel<AggregatedResource>(ro);
		WebMarkupContainer itemInfo = new WebMarkupContainer("itemInfo", itemModel);
		add(itemInfo);
		itemInfo.add(new ExternalLink("resourceURI", itemModel.<String> bind("URI.toString"), itemModel
				.<URI> bind("URI")));
		itemInfo.add(new Label("creator"));
		itemInfo.add(new Label("createdFormatted"));
		itemInfo.add(new Label("size"));
	}
}
