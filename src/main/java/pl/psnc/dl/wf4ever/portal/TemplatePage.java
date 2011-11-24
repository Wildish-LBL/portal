package pl.psnc.dl.wf4ever.portal;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class TemplatePage
	extends WebPage
{

	private static final long serialVersionUID = 1L;


	public TemplatePage(final PageParameters parameters)
	{
		add(new BookmarkablePageLink<Void>("menu-home", HomePage.class));
		add(new BookmarkablePageLink<Void>("menu-myros", MyRosPage.class));
	}
}
