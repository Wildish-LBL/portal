package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.markup.html.WebMarkupContainer;
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


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public ButtonsBar(String id) {
        super(id);
        setOutputMarkupId(true);
        folderButtonsContainer = new WebMarkupContainer("folder-buttons-container");
        resourceButtonsContainer = new WebMarkupContainer("resource-buttons-container");
        folderButtonsContainer.setVisible(false);
        resourceButtonsContainer.setVisible(false);
        add(folderButtonsContainer);
        add(resourceButtonsContainer);
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
}
