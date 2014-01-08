package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Folder;

import pl.psnc.dl.wf4ever.portal.components.form.ProtectedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddClickedEvent;

/**
 * A panel aggregating action buttons for a folder - add a new folder or resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class FolderActionsPanel extends Panel {

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
     */
    public FolderActionsPanel(String id, final IModel<Folder> folderModel) {
        super(id, folderModel);

        setOutputMarkupId(true);
        form = new Form<Void>("form");
        add(form);
        form.add(new ProtectedAjaxEventButton("add-resource", form, null, ResourceAddClickedEvent.class));
        form.add(new ProtectedAjaxEventButton("add-folder", form, null, FolderAddClickedEvent.class));
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof FolderChangeEvent) {
            onFolderChange((FolderChangeEvent) event.getPayload());
        }
    }


    /**
     * Refresh this panel when the current folder changes.
     * 
     * @param event
     *            AJAX event
     */
    private void onFolderChange(FolderChangeEvent event) {
        event.getTarget().add(this);
    }

}
