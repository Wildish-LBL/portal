package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.purl.wf4ever.rosrs.client.Thing;

import pl.psnc.dl.wf4ever.portal.ui.components.UniversalStyledAjaxButton;

/**
 * Buttons bar for selected folder or resource.
 * 
 * @author pejot
 * 
 */
public class ResourceButtonsBar extends Panel {

    /** Serialziation. */
    private static final long serialVersionUID = 1L;
    /** Folder buttons container. */
    WebMarkupContainer folderButtonsContainer;
    /** Resource buttons container. */
    WebMarkupContainer resourceButtonsContainer;
    /** Add resource button. */
    UniversalStyledAjaxButton addResource;
    /** Delete folder button. */
    UniversalStyledAjaxButton deleteFolder;
    /** Download folder button. */
    UniversalStyledAjaxButton downloadFolder;
    /** Edit resource button. */
    UniversalStyledAjaxButton editResource;
    /** Download resource button. */
    UniversalStyledAjaxButton downloadResource;
    /** Delete resource button. */
    UniversalStyledAjaxButton deleteResource;
    /** Fake form for ajax buttons. */
    Form<?> roForm;
    /** Processed Thing. */
    private Thing processedThing;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public ResourceButtonsBar(String id) {
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
        processedThing = folder;
        folderButtonsContainer.setVisible(true);
    }


    /**
     * Show folder buttons.
     * 
     * @param resource
     *            processed resource
     */
    public void showResourceButtonsContainer(Thing resource) {
        processedThing = resource;
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
        addResource = new UniversalStyledAjaxButton("add-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                target.appendJavaScript("$('#upload-resource-modal').modal('show')");
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        downloadFolder = new UniversalStyledAjaxButton("download-folder", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        deleteFolder = new UniversalStyledAjaxButton("delete-folder", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
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
        editResource = new UniversalStyledAjaxButton("edit-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        deleteResource = new UniversalStyledAjaxButton("delete-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }

        };
        downloadResource = new UniversalStyledAjaxButton("download-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }

        };
        resourceButtonsContainer.add(editResource);
        resourceButtonsContainer.add(deleteResource);
        resourceButtonsContainer.add(downloadResource);
        roForm.add(resourceButtonsContainer);
    }
}
