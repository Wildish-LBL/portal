package pl.psnc.dl.wf4ever.portal.components.form;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * A button the redirects to another page when clicked.
 * 
 * @author piotrekhol
 * 
 */
public class AjaxRedirectButton extends AjaxButton {

    /** id. */
    private static final long serialVersionUID = -2527416440222820413L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AjaxRedirectButton.class);

    /** Target page class. */
    private Class<? extends Page> pageClass;

    /** Target page params. */
    private PageParameters params;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     * @param form
     *            for which will be validated
     * @param pageClass
     *            target page class
     * @param params
     *            target page params
     */
    public AjaxRedirectButton(String id, Form<?> form, Class<? extends Page> pageClass, PageParameters params) {
        super(id, form);
        this.pageClass = pageClass;
        this.params = params;
    }


    @Override
    protected final void onSubmit(AjaxRequestTarget target, Form<?> form) {
        target.appendJavaScript("hideBusy()");
        throw new RestartResponseException(pageClass, params);
    }


    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        target.appendJavaScript("hideBusy()");
        LOG.error("Error when submitting the button");
    }


    @Override
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return new AjaxCallDecorator() {

            private static final long serialVersionUID = 3361600615366656231L;


            @Override
            public CharSequence decorateScript(Component c, CharSequence script) {
                return "showBusy(); " + script;
            }
        };
    }

}
