package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@AuthorizeInstantiation("USER")
public class MyRosPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;


	public MyRosPage(final PageParameters parameters)
	{
		super(parameters);
		add(new BookmarkablePageLink<Void>("myExpImport", MyExpAuthorizePage.class));
	}
}
