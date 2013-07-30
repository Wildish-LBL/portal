package pl.psnc.dl.wf4ever.portal.modals;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.events.ros.RoDeleteReadyEvent;

import com.google.common.eventbus.EventBus;

/**
 * A modal for adding resources to the RO.
 * 
 * @author piotrekhol
 * 
 */
public class DeleteROModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = 2193789648186156745L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param eventBusModel
     *            event bus
     * @param toDelete
     *            ROs to delete
     */
    public DeleteROModal(String id, final IModel<EventBus> eventBusModel,
            final IModel<? extends List<ResearchObject>> toDelete) {
        super(id, toDelete, eventBusModel, "delete-ro-modal", "Confirm");

        modal.add(new Label("deleteCnt", new AbstractReadOnlyModel<String>() {

            /** id. */
            private static final long serialVersionUID = -3411390081180717367L;


            @Override
            public String getObject() {
                if (toDelete.getObject().size() == 1) {
                    return "1 Research Object";
                }
                return toDelete.getObject().size() + " Research Objects";
            }
        }));
    }


    @Override
    public void onOk(AjaxRequestTarget target) {
        eventBusModel.getObject().post(new RoDeleteReadyEvent(target));
        hide(target);
    }
}
