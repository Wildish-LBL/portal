package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxDecoratedButton;

import com.google.common.eventbus.EventBus;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
public abstract class AbstractModal extends EventPanel {

    /** id. */
    private static final long serialVersionUID = -278196023603190873L;

    /** Feedback panel. */
    protected MyFeedbackPanel feedbackPanel;

    /** Form for all input fields and buttons. */
    protected Form<?> form;

    /** Modal id in HTML for JavaScript hiding and showing. */
    private String modalId;

    /** The div with a different ID for each modal. */
    protected WebMarkupContainer modal;

    /** Component that will receive focus on show. */
    protected Component componentWithFocus = null;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param eventBusModel
     *            event bus
     * @param modalId
     *            modal HTML id, without any '#'
     * @param title
     *            modal title
     */
    public AbstractModal(String id, IModel<EventBus> eventBusModel, String modalId, String title) {
        this(id, null, eventBusModel, modalId, title);
    }


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            panel model, may be null
     * @param eventBusModel
     *            event bus
     * @param modalId
     *            modal HTML id, without any '#'
     * @param title
     *            modal title
     */
    public AbstractModal(String id, IModel<?> model, final IModel<EventBus> eventBusModel, String modalId, String title) {
        super(id, model, eventBusModel);
        setOutputMarkupId(true);
        this.modalId = modalId;
        form = new Form<Void>("form");
        add(form);

        modal = new WebMarkupContainer("modal");
        modal.add(AttributeAppender.replace("id", modalId));
        form.add(modal);

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        modal.add(feedbackPanel);

        modal.add(new Label("title", title));

        AjaxButton ok = new AjaxDecoratedButton("ok", form) {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClicked(AjaxRequestTarget target, Form<?> form) {
                onOk(target);
            }
        };
        form.setDefaultButton(ok);
        modal.add(ok);
        modal.add(new AjaxDecoratedButton("cancel", form) {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClicked(AjaxRequestTarget target, Form<?> form) {
                onCancel(target);
            }
        }.setDefaultFormProcessing(false));
        modal.add(new AjaxDecoratedButton("close", form) {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            public void onClicked(AjaxRequestTarget target, Form<?> form) {
                onCancel(target);
            }
        }.setDefaultFormProcessing(false));
    }


    /**
     * Post an event and hide.
     * 
     * @param target
     *            AJAX target
     */
    protected abstract void onOk(AjaxRequestTarget target);


    /**
     * Hide.
     * 
     * @param target
     *            AJAX target
     */
    protected void onCancel(AjaxRequestTarget target) {
        hide(target);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forScript("$(document).ready(function() {\n" + "    $('#" + modalId
                + "').modal({\n" + "        backdrop : 'static',\n" + "        show : false\n" + "    });\n" + "});\n"
                + "", "init-modal"));
    }


    /**
     * Show itself.
     * 
     * @param target
     *            AJAX target
     */
    public void show(AjaxRequestTarget target) {
        target.add(this);
        target.appendJavaScript("$('#" + modalId + "').modal('show')");
        if (componentWithFocus != null) {
            target.focusComponent(componentWithFocus);
        }
    }


    /**
     * Set this component to have focus on show.
     * 
     * @param component
     *            form component
     * @return the same component
     */
    protected Component withFocus(Component component) {
        componentWithFocus = component;
        componentWithFocus.setOutputMarkupId(true);
        return componentWithFocus;
    }


    /**
     * Hide.
     * 
     * @param target
     *            AJAX target
     */
    public void hide(AjaxRequestTarget target) {
        target.prependJavaScript("$('#" + modalId + "').modal('hide')");
        target.add(feedbackPanel);
    }

}
