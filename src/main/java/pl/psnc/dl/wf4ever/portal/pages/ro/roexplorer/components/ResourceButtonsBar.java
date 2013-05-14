package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.Thing;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.ui.components.UniversalStyledAjaxButton;

/**
 * Buttons bar for selected folder or resource.
 * 
 * @author pejot
 * 
 */
public class ResourceButtonsBar extends Panel {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ResourceButtonsBar.class);
    /** Serialization. */
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
    /** list of components to refresh. */
    private List<Component> targets;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public ResourceButtonsBar(String id) {
        super(id);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
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
                super.onSubmit(target, form);
            }

        };
        downloadFolder = new UniversalStyledAjaxButton("download-folder", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }

        };
        deleteFolder = new UniversalStyledAjaxButton("delete-folder", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
            }

        };

        folderButtonsContainer.add(addResource);
        //folderButtonsContainer.add(downloadFolder);
        //folderButtonsContainer.add(deleteFolder);
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
                super.onSubmit(target, form);
            }

        };
        deleteResource = new UniversalStyledAjaxButton("delete-resource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if (processedThing instanceof Resource) {
                    try {
                        ((Resource) processedThing).delete();
                    } catch (ROSRSException e) {
                        LOG.error("Can not remove resource: " + processedThing.getUri(), e);
                        return;
                    }
                    for (Component c : targets) {
                        target.add(c);
                    }
                }
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
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
        //resourceButtonsContainer.add(editResource);
        resourceButtonsContainer.add(deleteResource);
        //resourceButtonsContainer.add(downloadResource);
        roForm.add(resourceButtonsContainer);

    }


    /**
     * set the list of component to refresh once the button is clicked.
     * 
     * @param component
     *            refreshable component.
     */
    public void appendTarget(Component component) {
        if (targets == null) {
            targets = new ArrayList<Component>();
        }
        targets.add(component);
    }
}
