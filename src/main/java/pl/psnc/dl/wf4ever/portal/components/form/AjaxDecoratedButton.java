package pl.psnc.dl.wf4ever.portal.components.form;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;

/**
 * A button the creates shows a "busy" indicator when clicked.
 * 
 * @author piotrekhol
 * 
 */
public abstract class AjaxDecoratedButton extends AjaxButton {

    /** id. */
    private static final long serialVersionUID = -2527416440222820413L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AjaxDecoratedButton.class);


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     * @param form
     *            for which will be validated
     */
    public AjaxDecoratedButton(String id, Form<?> form) {
        super(id, form);
    }


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     */
    public AjaxDecoratedButton(String id) {
        super(id);
    }


    @Override
    public final void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
        target.appendJavaScript("hideBusy()");
        onClicked(target, form);
    }


    /**
     * Called after the form has been submitted.
     * 
     * @param target
     *            AJAX target
     * @param form
     *            form to which the button belongs
     */
    public abstract void onClicked(AjaxRequestTarget target, Form<?> form);


    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        target.appendJavaScript("hideBusy()");
        LOG.error("Error when submitting the button");
    }


    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        AjaxCallListener myAjaxCallListener = new AjaxCallListener() {

            /** id. */
            private static final long serialVersionUID = -5008615244332637745L;


            @Override
            public CharSequence getBeforeHandler(Component component) {
                return "showBusy();";
            }
        };
        attributes.getAjaxCallListeners().add(myAjaxCallListener);
    }

}
