package pl.psnc.dl.wf4ever.portal;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;

public class WicketHomePage extends WebPage {
	private static final long serialVersionUID = 1L;

    public WicketHomePage(final PageParameters parameters) {
		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));
        // TODO Add your page's components here
    }
}
