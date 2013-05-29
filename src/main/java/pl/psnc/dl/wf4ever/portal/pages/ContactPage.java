package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.request.mapper.parameter.PageParameters;


/**
 * A static page.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ContactPage extends BasePage {

    /** id. */
    private static final long serialVersionUID = 2960549491810135112L;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page params
     */
    public ContactPage(PageParameters pageParameters) {
        super(pageParameters);
    }


    /**
     * Constructor.
     */
    public ContactPage() {
        this(new PageParameters());
    }

}
