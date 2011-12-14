package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.services.ROSRService;

public class HomePage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;


	public HomePage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);

		List<URI> uris = ROSRService.getROList();
		ListView<URI> list = new ListView<URI>("10randomROsListView", uris.subList(0, Math.min(uris.size(), 10))) {

			private static final long serialVersionUID = -1782790193913483327L;


			@Override
			protected void populateItem(ListItem<URI> item)
			{
				item.add(new Label("title", ((URI) item.getDefaultModelObject()).toString()));
			}
		};
		list.setReuseItems(true);
		add(list);

		add(new Label("roCnt", "" + uris.size()));
	}
}
