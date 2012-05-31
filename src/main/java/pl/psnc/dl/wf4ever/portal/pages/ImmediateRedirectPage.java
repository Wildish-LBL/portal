package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ImmediateRedirectPage extends WebPage {

    private static final long serialVersionUID = -734520058782611499L;


    public ImmediateRedirectPage() {

    }


    public ImmediateRedirectPage(PageParameters pageParameters) {
        throw new RedirectToUrlException(pageParameters.get("redirectTo").toString());
    }
}