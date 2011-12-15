package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

@AuthorizeInstantiation("USER")
public class MyRosPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private final Form< ? > form;


	public MyRosPage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);

		List<URI> uris = ROSRService.getROList(MySession.get().getdLibraAccessToken());

		form = new Form<Void>("form");
		add(form);
		CheckGroup<URI> group = new CheckGroup<URI>("group", uris);
		form.add(group);
		ListView<URI> list = new ListView<URI>("rosListView", uris) {

			private static final long serialVersionUID = -6310254217773728128L;


			@Override
			protected void populateItem(ListItem<URI> item)
			{
				item.add(new Label("title", ((URI) item.getDefaultModelObject()).toString()));
			}
		};
		list.setReuseItems(true);
		group.add(list);

		form.add(new BookmarkablePageLink<Void>("myExpImport", MyExpAuthorizePage.class));
	}
}
