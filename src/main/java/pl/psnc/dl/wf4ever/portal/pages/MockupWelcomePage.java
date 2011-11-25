package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MockupWelcomePage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;


	public MockupWelcomePage(final PageParameters parameters)
	{
		super(parameters);
		add(new BookmarkablePageLink<Void>("home", HomePage.class));
		add(new BookmarkablePageLink<Void>("myros", MyRosPage.class));
		add(new BookmarkablePageLink<Void>("ro", RoPage.class));
	}
}
