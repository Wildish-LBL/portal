package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.components.annotations.TemplateDropDownChoice;
import pl.psnc.dl.wf4ever.portal.model.template.ResearchObjectTemplate;

import com.google.common.eventbus.EventBus;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
public class CreateROModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = 6366655308600651088L;

    /** New RO id. */
    private String roId;

    /** RO template. */
    private ResearchObjectTemplate template;

    /** RO title. */
    private String title;

    /** RO description. */
    private String description;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param eventBusModel
     *            event bus
     */
    public CreateROModal(String id, final IModel<EventBus> eventBusModel) {
        super(id, eventBusModel, "create-ro-modal", "Create RO");
        modal.add(withFocus(new RequiredTextField<String>("roId", new PropertyModel<String>(this, "roId"))));
        TemplateDropDownChoice templates = new TemplateDropDownChoice("templates",
                new PropertyModel<ResearchObjectTemplate>(this, "template"));
        final Label templateDescription = new Label("template-description", new PropertyModel<String>(this,
                "template.description"));
        templateDescription.setOutputMarkupId(true);
        modal.add(templates);
        modal.add(templateDescription);
        templates.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(templateDescription);
            }
        });

        modal.add(new TextField<String>("ro-title", new PropertyModel<String>(this, "title")));
        modal.add(new TextArea<String>("description", new PropertyModel<String>(this, "description")));
    }


    /**
     * Post an event and hide.
     * 
     * @param target
     *            AJAX target
     */
    @Override
    public void onOk(AjaxRequestTarget target) {
        if (title != null && title.trim().isEmpty()) {
            title = null;
        }
        if (description != null && description.trim().isEmpty()) {
            description = null;
        }
        onApply(target, roId, template, title, description);
        hide(target);
    }


    /**
     * Ready to create the RO.
     * 
     * @param target
     *            response target
     * @param roId
     *            RO id
     * @param template
     *            RO template (may be null)
     * @param title
     *            RO title
     * @param description
     *            RO description
     */
    public void onApply(AjaxRequestTarget target, String roId, ResearchObjectTemplate template, String title,
            String description) {
    }


    /**
     * Hide.
     * 
     * @param target
     *            AJAX target
     */
    @Override
    public void onCancel(AjaxRequestTarget target) {
        hide(target);
    }


    public String getRoId() {
        return roId;
    }


    public void setRoId(String roId) {
        this.roId = roId;
    }


    public ResearchObjectTemplate getTemplate() {
        return template;
    }


    public void setTemplate(ResearchObjectTemplate template) {
        this.template = template;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
