package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.pages.util.ModelIteratorAdapter;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

@AuthorizeInstantiation("USER")
public class MyRosPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(MyRosPage.class);

	final List<ResearchObject> selectedResearchObjects = new ArrayList<ResearchObject>();

	private String roId;

	private MyFeedbackPanel addFeedbackPanel;

	private MyFeedbackPanel deleteFeedbackPanel;


	@SuppressWarnings("serial")
	public MyRosPage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);

		add(new MyFeedbackPanel("feedbackPanel"));

		List<URI> uris = ROSRService.getROList(rodlURI, MySession.get().getdLibraAccessToken());
		final List<ResearchObject> researchObjects = new ArrayList<ResearchObject>();
		for (URI uri : uris) {
			try {
				researchObjects
						.add(RoFactory.createResearchObject(rodlURI, uri, false, MySession.get().getUsernames()));
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
		RefreshingView<ResearchObject> list = new RefreshingView<ResearchObject>("rosListView") {

			private static final long serialVersionUID = -6310254217773728128L;


			@Override
			protected void populateItem(Item<ResearchObject> item)
			{
				AggregatedResource researchObject = (AggregatedResource) item.getDefaultModelObject();
				item.add(new Check<ResearchObject>("checkbox", item.getModel()));
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
				link.getPageParameters().add("ro",
					UrlEncoder.QUERY_INSTANCE.encode(researchObject.getURI().toString(), "UTF-8"));
				link.add(new Label("URI"));
				item.add(link);
				item.add(new Label("createdFormatted"));
			}


			@Override
			protected Iterator<IModel<ResearchObject>> getItemModels()
			{
				return new ModelIteratorAdapter<ResearchObject>(researchObjects.iterator()) {

					@Override
					protected IModel<ResearchObject> model(ResearchObject ro)
					{
						return new CompoundPropertyModel<ResearchObject>(ro);
					}
				};
			}

		};
		group.add(list);

		final Label deleteCntLabel = new Label("deleteCnt", new PropertyModel<String>(this, "deleteCnt"));
		deleteCntLabel.setOutputMarkupId(true);
		add(deleteCntLabel);

		final Form< ? > addForm = new Form<Void>("addForm");
		RequiredTextField<String> name = new RequiredTextField<String>("roId", new PropertyModel<String>(this, "roId"));
		name.add(new IValidator<String>() {

			@Override
			public void validate(IValidatable<String> validatable)
			{
				try {
					if (!ROSRService.isRoIdFree(((PortalApplication) getApplication()).getRodlURI(),
						validatable.getValue())) {
						validatable.error(new ValidationError().setMessage("This ID is already in use"));
					}
				}
				catch (Exception e) {
					log.error(e);
					// assume it's ok
				}
			}

		});
		addForm.add(name);
		add(addForm);

		addFeedbackPanel = new MyFeedbackPanel("addFeedbackPanel");
		addFeedbackPanel.setOutputMarkupId(true);
		addForm.add(addFeedbackPanel);

		form.add(new MyAjaxButton("delete", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				form.process(null);
				if (!selectedResearchObjects.isEmpty()) {
					target.add(deleteCntLabel);
					target.appendJavaScript("$('#confirm-delete-modal').modal('show')");
				}
			}
		});

		deleteFeedbackPanel = new MyFeedbackPanel("deleteFeedbackPanel");
		deleteFeedbackPanel.setOutputMarkupId(true);
		add(deleteFeedbackPanel);

		add(new MyAjaxButton("confirmDelete", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				Token dLibraToken = MySession.get().getdLibraAccessToken();
				for (AggregatedResource ro : selectedResearchObjects) {
					try {
						ROSRService.deleteResearchObject(ro.getURI(), dLibraToken);
						researchObjects.remove(ro);
					}
					catch (Exception e) {
						error("Could not delete Research Object: " + ro.getURI() + " (" + e.getMessage() + ")");
					}
				}
				target.add(form);
				target.add(deleteFeedbackPanel);
				target.appendJavaScript("$('#confirm-delete-modal').modal('hide')");
			}
		});

		add(new MyAjaxButton("cancelDelete", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#confirm-delete-modal').modal('hide')");
			}
		});

		form.add(new MyAjaxButton("add", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#confirm-add-modal').modal('show')");
			}
		});

		addForm.add(new MyAjaxButton("confirmAdd", addForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > addForm)
			{
				super.onSubmit(target, addForm);
				Token dLibraToken = MySession.get().getdLibraAccessToken();
				try {
					URI researchObjectURI = ROSRService.createResearchObject(
						((PortalApplication) getApplication()).getRodlURI(), roId, dLibraToken).getLocation();
					researchObjects.add(RoFactory.createResearchObject(rodlURI, researchObjectURI, false, MySession
							.get().getUsernames()));
				}
				catch (URISyntaxException e) {
					error("Could not add Research Object: " + roId + " (" + e.getMessage() + ")");
				}
				target.add(form);
				target.add(addFeedbackPanel);
				target.appendJavaScript("$('#confirm-add-modal').modal('hide')");
			}


			@Override
			protected void onError(AjaxRequestTarget target, Form< ? > form)
			{
				super.onError(target, form);
				target.add(addFeedbackPanel);
			}
		});

		addForm.add(new MyAjaxButton("cancelAdd", addForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#confirm-add-modal').modal('hide')");
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
	 * @param roId
	 *            the roId to set
	 */
	public void setRoId(String roId)
	{
		this.roId = roId;
	}

}
