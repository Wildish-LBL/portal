package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.purl.wf4ever.rosrs.client.Thing;

/**
 * Buttons bar for selected folder or resource.
 * 
 * @author pejot
 * 
 */
public class ButtonsBar extends Panel {

    /** Folder buttons container. */
    WebMarkupContainer folderButtonsContainer;
    /** Resource buttons container. */
    WebMarkupContainer resourceButtonsContainer;
    Button addResource;
    Button deleteFolder;
    Button downloadFolder;

    Button editResource;
    Button downloadResource;
    Button deleteResource;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public ButtonsBar(String id) {
        super(id);
        setOutputMarkupId(true);
        folderButtonsContainerInit();
        resourceButtonsContainerInit();
    }


    /**
     * Show folder buttons.
     * 
     * @param folder
     *            processed folder
     */
    public void showFoldersButtonsContainer(Thing folder) {
        folderButtonsContainer.setVisible(true);
    }


    /**
     * Show folder buttons.
     * 
     * @param resource
     *            processed resource
     */
    public void showResourceButtonsContainer(Thing resource) {
        resourceButtonsContainer.setVisible(true);
    }


    /**
     * Hide folder buttons.
     * 
     */
    public void hideFoldersButtonContainer() {
        folderButtonsContainer.setVisible(false);
    }


    /**
     * Show folder buttons.
     * 
     */
    public void hideResourceButtonsContainer() {
        resourceButtonsContainer.setVisible(false);
    }


    /**
     * Initialize folder buttons container.
     */
    private void folderButtonsContainerInit() {
        folderButtonsContainer = new WebMarkupContainer("folder-buttons-container");
        folderButtonsContainer.setVisible(false);
        addResource = new Button("add-resource");
        downloadFolder = new Button("download-folder");
        deleteFolder = new Button("delete-folder");

        folderButtonsContainer.add(addResource);
        folderButtonsContainer.add(downloadFolder);
        folderButtonsContainer.add(deleteFolder);
        add(folderButtonsContainer);

    }


    /**
     * Initialize resource buttons container.
     */
    private void resourceButtonsContainerInit() {
        resourceButtonsContainer = new WebMarkupContainer("resource-buttons-container");
        resourceButtonsContainer.setVisible(false);
        editResource = new Button("edit-resource");
        deleteResource = new Button("delete-resource");
        downloadResource = new Button("download-resource");
        resourceButtonsContainer.add(editResource);
        resourceButtonsContainer.add(deleteResource);
        resourceButtonsContainer.add(downloadResource);
        add(resourceButtonsContainer);
    }
}
