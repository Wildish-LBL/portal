package pl.psnc.dl.wf4ever.portal.components;

import java.util.Objects;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.google.common.eventbus.EventBus;

/**
 * A panel that registers itself to the EventBus.
 * 
 * @author piotrekhol
 * 
 */
public class EventPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = 9211139434766774723L;

    /** Event bus. */
    protected IModel<EventBus> eventBusModel;


    /**
     * Constructor.
     * 
     * @param id
     *            The non-null id of this component
     * @param model
     *            The component's model
     * @param eventBusModel
     *            The non-null event bus model
     */
    public EventPanel(String id, IModel<?> model, IModel<EventBus> eventBusModel) {
        super(id, model);
        this.eventBusModel = Objects.requireNonNull(eventBusModel, "The event bus model cannot be null");
    }


    @Override
    protected void onConfigure() {
        eventBusModel.getObject().register(this);
        super.onConfigure();
    }

}
