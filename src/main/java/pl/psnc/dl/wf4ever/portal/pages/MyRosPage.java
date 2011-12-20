package pl.psnc.dl.wf4ever.portal.pages;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.PatternValidator;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

@AuthorizeInstantiation("USER")
public class MyRosPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	final List<ResearchObject> selectedResearchObjects = new ArrayList<ResearchObject>();

	private String roId;


	public MyRosPage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);

		List<URI> uris = ROSRService.getROList(MySession.get().getdLibraAccessToken());
		final List<ResearchObject> researchObjects = new ArrayList<ResearchObject>();
		for (URI uri : uris) {
			try {
				researchObjects.add(new RoFactory(uri).createResearchObject());
			}
			catch (Exception e) {
				error("Could not get manifest for: " + uri + " (" + e.getMessage() + ")");
			}
		}

		final Form< ? > form = new Form<Void>("form");
		form.setOutputMarkupId(true);
		add(form);
		form.add(new MyFeedbackPanel("feedbackPanel"));
		CheckGroup<ResearchObject> group = new CheckGroup<ResearchObject>("group", selectedResearchObjects);
		form.add(group);
		ListView<ResearchObject> list = new ListView<ResearchObject>("rosListView", researchObjects) {

			private static final long serialVersionUID = -6310254217773728128L;


			@Override
			protected void populateItem(ListItem<ResearchObject> item)
			{
				ResearchObject researchObject = (ResearchObject) item.getDefaultModelObject();
				item.add(new Check<ResearchObject>("checkbox", item.getModel()));
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
				link.getPageParameters().add("ro",
					UrlEncoder.QUERY_INSTANCE.encode(researchObject.getURI().toString(), "UTF-8"));
				link.add(new Label("title", researchObject.getURI().toString()));
				item.add(link);
				item.add(new Label("created", new PropertyModel<String>(researchObject, "createdFormatted")));
			}
		};
		list.setReuseItems(true);
		group.add(list);

		final Label deleteCntLabel = new Label("deleteCnt", new PropertyModel<String>(this, "deleteCnt"));
		deleteCntLabel.setOutputMarkupId(true);
		add(deleteCntLabel);

		final Form< ? > addForm = new Form<Void>("addForm");
		RequiredTextField<String> name = new RequiredTextField<String>("roId", new PropertyModel<String>(this, "roId"));
		name.add(new PatternValidator("[\\w]+"));
		addForm.add(name);
		add(addForm);

		addForm.add(new MyFeedbackPanel("addFeedbackPanel"));

		form.add(new AjaxButton("delete", form) {

			private static final long serialVersionUID = 1735622302239127515L;


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				form.process(null);
				if (!selectedResearchObjects.isEmpty()) {
					target.add(deleteCntLabel);
					target.appendJavaScript("$('#confirm-delete-modal').modal('show')");
				}
			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
				// TODO Auto-generated method stub

			}
		});

		add(new AjaxButton("confirmDelete", form) {

			private static final long serialVersionUID = 1735622302239127515L;


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				Token dLibraToken = MySession.get().getdLibraAccessToken();
				for (ResearchObject ro : selectedResearchObjects) {
					try {
						ROSRService.deleteResearchObject(ro.getURI(), dLibraToken);
						researchObjects.remove(ro);
					}
					catch (OAuthException e) {
						error("Could not delete Research Object: " + ro.getURI() + " (" + e.getMessage() + ")");
					}
				}
				target.add(form);
				target.appendJavaScript("$('#confirm-delete-modal').modal('hide')");
			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
				// TODO Auto-generated method stub

			}
		});

		add(new AjaxButton("cancelDelete", form) {

			private static final long serialVersionUID = 1735622302239127515L;


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				target.appendJavaScript("$('#confirm-delete-modal').modal('hide')");
			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
				// TODO Auto-generated method stub

			}
		});

		form.add(new AjaxButton("add", form) {

			private static final long serialVersionUID = 1735622302239127515L;


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				target.appendJavaScript("$('#confirm-add-modal').modal('show')");
			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
				// TODO Auto-generated method stub

			}
		});

		addForm.add(new AjaxButton("confirmAdd", addForm) {

			private static final long serialVersionUID = 1735622302239127515L;


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > addForm)
			{
				Token dLibraToken = MySession.get().getdLibraAccessToken();
				try {
					URI researchObjectURI = ROSRService.createResearchObject(roId, dLibraToken, false);
					researchObjects.add(new RoFactory(researchObjectURI).createResearchObject());
				}
				catch (OAuthException | UnsupportedEncodingException | URISyntaxException e) {
					error("Could not add Research Object: " + roId + " (" + e.getMessage() + ")");
				}
				target.add(form);
				target.appendJavaScript("$('#confirm-add-modal').modal('hide')");
			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
				// TODO Auto-generated method stub

			}
		});

		addForm.add(new AjaxButton("cancelAdd", addForm) {

			private static final long serialVersionUID = 1735622302239127515L;


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				target.appendJavaScript("$('#confirm-add-modal').modal('hide')");
			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
				// TODO Auto-generated method stub

			}
		}.setDefaultFormProcessing(false));
		form.add(new BookmarkablePageLink<Void>("myExpImport", MyExpAuthorizePage.class));
	}


	public String getDeleteCnt()
	{
		if (selectedResearchObjects.size() == 1)
			return "1 Research Object";
		return selectedResearchObjects.size() + " Research Objects";
	}


	/**
	 * @return the roId
	 */
	public String getRoId()
	{
		return roId;
	}


	/**
	 * @param roId the roId to set
	 */
	public void setRoId(String roId)
	{
		this.roId = roId;
	}

}
