package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class TemplatePage
	extends WebPage
{

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(TemplatePage.class);


	public TemplatePage(final PageParameters parameters)
	{
		getSession().bind();
		final WebMarkupContainer redirect = new WebMarkupContainer("redirect");
		String redirectionURL = parameters.get("redirectTo").toString();
		if (redirectionURL != null) {
			final String content = "3;URL=" + redirectionURL;
			redirect.add(new AttributeModifier("content", new Model<String>(content)));
		}
		else {
			redirect.setVisible(false);
		}
		add(redirect);

		add(new BookmarkablePageLink<Void>("menu-home", HomePage.class));
		add(new BookmarkablePageLink<Void>("menu-myros", MyRosPage.class));
	}

}
