package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.purl.wf4ever.rosrs.client.Thing;

/**
 * Buttons bar for selected folder or resource.
 * 
 * @author pejot
 * 
 */
public class ButtonsBar extends Panel {

    /** Serialziation. */
    private static final long serialVersionUID = 1L;
    /** Folder buttons container. */
    WebMarkupContainer folderButtonsContainer;
    /** Resource buttons container. */
    WebMarkupContainer resourceButtonsContainer;
    /** Add resource button. */
    AjaxButton addResource;
    /** Delete folder button. */
    AjaxButton deleteFolder;
    /** Download folder button. */
    AjaxButton downloadFolder;
    /** Edit resource button. */
    AjaxButton editResource;
    /** Download resource button. */
    AjaxButton downloadResource;
    /** Delete resource button. */
    AjaxButton deleteResource;
    /** Fake form for ajax buttons. */
    Form<?> roForm;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public ButtonsBar(String id) {
        super(id);
        setOutputMarkupId(true);
        roForm = new Form<Void>("roForm");
        add(roForm);
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
        addResource = new AjaxButton("add-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        downloadFolder = new AjaxButton("download-folder", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        deleteFolder = new AjaxButton("delete-folder", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };

        folderButtonsContainer.add(addResource);
        folderButtonsContainer.add(downloadFolder);
        folderButtonsContainer.add(deleteFolder);
        roForm.add(folderButtonsContainer);

    }


    /**
     * Initialize resource buttons container.
     */
    private void resourceButtonsContainerInit() {
        resourceButtonsContainer = new WebMarkupContainer("resource-buttons-container");
        resourceButtonsContainer.setVisible(false);
        editResource = new AjaxButton("edit-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        deleteResource = new AjaxButton("delete-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        downloadResource = new AjaxButton("download-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        resourceButtonsContainer.add(editResource);
        resourceButtonsContainer.add(deleteResource);
        resourceButtonsContainer.add(downloadResource);
        roForm.add(resourceButtonsContainer);
    }
}
