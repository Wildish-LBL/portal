package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * A utility page that redirects to a different URL.
 * 
 * @author piotrekhol
 * 
 */
public class ImmediateRedirectPage extends WebPage {

    /** id. */
    private static final long serialVersionUID = -734520058782611499L;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            must include "redirectTo" param with the URL
     */
    public ImmediateRedirectPage(PageParameters pageParameters) {
        throw new RedirectToUrlException(pageParameters.get("redirectTo").toString());
    }
}
