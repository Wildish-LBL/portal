package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

@AuthorizeInstantiation("USER")
public class MyRosPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");


	public MyRosPage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);

		List<URI> uris = ROSRService.getROList(MySession.get().getdLibraAccessToken());
		List<ResearchObject> researchObjects = new ArrayList<ResearchObject>();
		for (URI uri : uris) {
			try {
				researchObjects.add(new ResearchObject(uri));
			}
			catch (Exception e) {
				error("Could not get manifest for: " + uri + " (" + e.getMessage() + ")");
			}
		}
		final List<ResearchObject> selectedResearchObjects = new ArrayList<ResearchObject>();

		Form< ? > form = new Form<Void>("form");
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
				item.add(new Label("title", researchObject.getResearchObjectURI().toString()));
				item.add(new Label("created", sdf.format(researchObject.getCreated().getTime())));
			}
		};
		list.setReuseItems(true);
		group.add(list);

		form.add(new AjaxButton("delete", form) {

			private static final long serialVersionUID = 1735622302239127515L;


			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				form.process(null);
				for (ResearchObject ro : selectedResearchObjects) {
					try {
						ROSRService.deleteResearchObject(ro.getResearchObjectURI());
					}
					catch (OAuthException e) {
						error("Could not delete Research Object: " + ro.getResearchObjectURI() + " (" + e.getMessage()
								+ ")");
					}
				}
				target.add(form);
			}


			@Override
			protected void onError(AjaxRequestTarget arg0, Form< ? > arg1)
			{
				// TODO Auto-generated method stub

			}
		});
		form.add(new BookmarkablePageLink<Void>("myExpImport", MyExpAuthorizePage.class));
	}
}
