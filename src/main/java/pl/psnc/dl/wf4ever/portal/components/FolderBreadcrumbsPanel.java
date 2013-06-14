package pl.psnc.dl.wf4ever.portal.components;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Folder;

import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Bootstrap breadcrumb that shows the current location given a list of folders. If there are no folders, an information
 * is displayed instead.
 * 
 * @author piotrekhol
 * 
 */
public class FolderBreadcrumbsPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = 6161074268125343983L;

    /** "no folders" label. */
    private WebMarkupContainer noFolders;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     * @param model
     *            a list of folders model
     * @param folderModel
     *            the currently selected folder model, used when a user clicks on one of the folders
     * @param eventBusModel
     *            event bus model for clicks and refreshes
     */
    public FolderBreadcrumbsPanel(String id, final IModel<List<Folder>> model, final IModel<Folder> folderModel,
            final IModel<EventBus> eventBusModel) {
        super(id, model);
        eventBusModel.getObject().register(this);

        setOutputMarkupId(true);
        add(new ListView<Folder>("folder-item", model) {

            /** id. */
            private static final long serialVersionUID = 6756293180528006214L;


            @Override
            protected void populateItem(final ListItem<Folder> item) {
                final Folder folder = item.getModelObject();
                String name = folder.getName();
                if (name.endsWith("/")) {
                    name = name.substring(0, name.length() - 1);
                }
                AjaxLink<String> link = new AjaxLink<String>("folder-link", new PropertyModel<String>(folder, "name")) {

                    /** id. */
                    private static final long serialVersionUID = 3495158432497901769L;


                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        folderModel.setObject(folder);
                        eventBusModel.getObject().post(new FolderChangeEvent(target));
                    }
                };
                item.add(link);
                link.add(new Label("folder", name));
                WebMarkupContainer divider = new WebMarkupContainer("divider");
                item.add(divider);
                divider.setVisible(item.getIndex() < model.getObject().size() - 1);
            }

        });
        noFolders = new WebMarkupContainer("no-folders");
        add(noFolders);
    }


    @Override
    protected void onConfigure() {
        noFolders.setVisible(((List<?>) getDefaultModelObject()).isEmpty());
    }


    /**
     * Refresh when the current folder changes.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onFolderChange(FolderChangeEvent event) {
        event.getTarget().add(this);
    }

}
