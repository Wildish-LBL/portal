package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Folder;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddClickedEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A panel aggregating action buttons for a folder - add a new folder or resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class FolderActionsPanel extends EventPanel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(FolderActionsPanel.class);

    /** A form for the buttons. */
    private Form<Void> form;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param folderModel
     *            current folder
     * @param eventBusModel
     *            event bus model for button clicks
     */
    public FolderActionsPanel(String id, final IModel<Folder> folderModel, final IModel<EventBus> eventBusModel) {
        super(id, folderModel, eventBusModel);

        setOutputMarkupId(true);
        form = new Form<Void>("form");
        add(form);
        form.add(new AuthenticatedAjaxEventButton("add-resource", form, eventBusModel, ResourceAddClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("add-folder", form, eventBusModel, FolderAddClickedEvent.class));
    }


    /**
     * Refresh this panel when the current folder changes.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onFolderChange(FolderChangeEvent event) {
        event.getTarget().add(this);
    }

}
