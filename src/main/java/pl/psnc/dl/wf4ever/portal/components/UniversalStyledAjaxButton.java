package pl.psnc.dl.wf4ever.portal.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;

import pl.psnc.dl.wf4ever.portal.MySession;

/**
 * A specific {@link AjaxButton} that performs certain JavaScript functions during actions.
 * 
 * @author piotrhol
 * 
 */
public abstract class UniversalStyledAjaxButton extends AjaxButton {

    /** id. */
    private static final long serialVersionUID = 6819868729651344345L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param form
     *            owning form
     */
    @SuppressWarnings("serial")
    public UniversalStyledAjaxButton(String id, Form<?> form) {
        super(id, form);
        add(new Behavior() {

            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                super.onComponentTag(component, tag);
                if (!component.isEnabled()) {
                    tag.append("class", "disabled", " ");
                }
            }
        });

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.wicket.ajax.markup.html.form.AjaxButton#onSubmit(org.apache.wicket.ajax
     * .AjaxRequestTarget, org.apache.wicket.markup.html.form.Form)
     */
    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        target.appendJavaScript("hideBusy()");
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.wicket.ajax.markup.html.form.AjaxButton#onError(org.apache.wicket.ajax
     * .AjaxRequestTarget, org.apache.wicket.markup.html.form.Form)
     */
    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        target.appendJavaScript("hideBusy()");
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


    @Override
    public MySession getSession() {
        return (MySession) super.getSession();
    }

}
