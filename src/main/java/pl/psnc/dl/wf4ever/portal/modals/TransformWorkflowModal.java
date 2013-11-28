package pl.psnc.dl.wf4ever.portal.modals;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Folder;

import pl.psnc.dl.wf4ever.portal.events.aggregation.AggregationChangedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMoveEvent;

/**
 * A modal window for moving a resource to a folder.
 * 
 * @author piotrekhol
 * 
 */
public class TransformWorkflowModal extends AbstractModal {

    /** id. */
    private static final long serialVersionUID = 5398436522469957609L;

    /** Folder selected by the user. */
    private Folder folder = null;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param foldersModel
     *            a list of folders to choose from
     */
    public TransformWorkflowModal(String id, IModel<List<Folder>> foldersModel) {
        super(id, foldersModel, "transform-workflow-modal", "Transform a workflow");
        modal.add(withFocus(new DropDownChoice<Folder>("mainFolder", new PropertyModel<Folder>(this, "mainFolder"),
                foldersModel, new ChoiceRenderer<Folder>("path", "uri"))));
        modal.add(withFocus(new DropDownChoice<Folder>("nestedFolder", new PropertyModel<Folder>(this, "nestedFolder"),
                foldersModel, new ChoiceRenderer<Folder>("path", "uri"))));
        modal.add(withFocus(new DropDownChoice<Folder>("scriptsFolder", new PropertyModel<Folder>(this, "scriptsFolder"),
                foldersModel, new ChoiceRenderer<Folder>("path", "uri"))));
        modal.add(withFocus(new DropDownChoice<Folder>("servicesFolder", new PropertyModel<Folder>(this, "servicesFolder"),
                foldersModel, new ChoiceRenderer<Folder>("path", "uri"))));
        // TODO:  Selcet good defaults, e.g. "config/scripts" if they exist -
        // or some magic "--none--" value if not to disable that particular extraction.
    }


    @Override
    public void onOk(AjaxRequestTarget target) {
        if (folder != null) {
            // TODO: Call the right event etc to do the actual transformatoin. this  is now
            // done in 
            
            //send(getPage(), Broadcast.BREADTH, new ResourceMoveEvent(target, folder));
        }
        hide(target);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof AggregationChangedEvent) {
            onAggregationChanged((AggregationChangedEvent) event.getPayload());
        }
    }


    /**
     * Called when the list of folders may have changed.
     * 
     * @param event
     *            AJAX event
     */
    public void onAggregationChanged(AggregationChangedEvent event) {
        event.getTarget().add(this);
    }


    public Folder getFolder() {
        return folder;
    }


    public void setFolder(Folder folder) {
        this.folder = folder;
    }

}
