package pl.psnc.dl.wf4ever.portal.modals;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.components.annotations.TemplateDropDownChoice;
import pl.psnc.dl.wf4ever.portal.events.CancelClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.OkClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoCreateClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoCreateReadyEvent;
import pl.psnc.dl.wf4ever.portal.model.template.ResearchObjectTemplate;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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
        modal.add(new RequiredTextField<String>("roId", new PropertyModel<String>(this, "roId")));
        TemplateDropDownChoice templates = new TemplateDropDownChoice("templates",
                new PropertyModel<ResearchObjectTemplate>(this, "template"));
        final Label description = new Label("template-description", new PropertyModel<String>(this,
                "template.description"));
        description.setOutputMarkupId(true);
        modal.add(templates);
        modal.add(description);
        templates.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(description);
            }
        });
    }


    /**
     * Show itself.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onRoCreate(RoCreateClickedEvent event) {
        show(event.getTarget());
    }


    /**
     * Post an event and hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onOk(OkClickedEvent event) {
        eventBusModel.getObject().post(new RoCreateReadyEvent(event.getTarget(), roId, template));
        hide(event.getTarget());
    }


    /**
     * Hide.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onCancel(CancelClickedEvent event) {
        hide(event.getTarget());
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
}
