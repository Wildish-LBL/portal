package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.FolderEntry;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.FolderBreadcrumbsPanel;
import pl.psnc.dl.wf4ever.portal.components.FolderContentsPanel;
import pl.psnc.dl.wf4ever.portal.components.annotations.AdvancedAnnotationsPanel;
import pl.psnc.dl.wf4ever.portal.components.annotations.CommentsList;
import pl.psnc.dl.wf4ever.portal.events.AddLinkEvent;
import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.DuplicateEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeleteClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeletedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMoveEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMovedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.UpdateClickedEvent;
import pl.psnc.dl.wf4ever.portal.modals.AddFolderModal;
import pl.psnc.dl.wf4ever.portal.modals.MoveResourceModal;
import pl.psnc.dl.wf4ever.portal.modals.UploadResourceModal;
import pl.psnc.dl.wf4ever.portal.model.FolderHierarchyModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A panel for exploring the RO. On the left side there is a list of folders/files. The user selects one folder at a
 * time, if there are any. The folder is the selected resource unless the user selects a resource inside this folder.
 * 
 * On the right there is a summary of the currently selected folder/file.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class RoContentPanel extends EventPanel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RoContentPanel.class);

    /** The currently viewed folder. */
    private Folder currentFolder;

    /** The currently selected resource (can be a folder). */
    private Resource currentResource;

    /** A list of visited folders. */
    private FolderHierarchyModel folderHierarchyModel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            research object model
     * @param eventBusModel
     *            event bus model for changes to the RO coming out of this panel
     */
    public RoContentPanel(String id, final IModel<ResearchObject> model, IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        IModel<Folder> folderModel = new PropertyModel<Folder>(this, "currentFolder");
        IModel<Resource> resourceModel = new PropertyModel<Resource>(this, "currentResource");
        IModel<List<Resource>> unrootedResourcesModel = new PropertyModel<List<Resource>>(getDefaultModel(),
                "resourcesWithoutFolders");
        IModel<List<Folder>> rootFolders = new PropertyModel<List<Folder>>(getDefaultModel(), "rootFolders");
        IModel<List<Folder>> allFolders = new PropertyModel<List<Folder>>(getDefaultModel(), "allFolders");
        folderHierarchyModel = new FolderHierarchyModel(folderModel);

        add(new FolderBreadcrumbsPanel("folder-breadcrumbs", folderHierarchyModel, folderModel, eventBusModel));
        add(new FolderActionsPanel("folder-actions", folderModel, eventBusModel));
        add(new FolderContentsPanel("folder-contents", folderModel, resourceModel, rootFolders, unrootedResourcesModel,
                eventBusModel));
        add(new ResourceActionsPanel("resource-actions", resourceModel, eventBusModel));
        add(new ResourceSummaryPanel("resource-summary", resourceModel, eventBusModel));
        add(new CommentsList("resource-comments", resourceModel, eventBusModel));
        add(new AdvancedAnnotationsPanel("advanced-annotations", "resource-basic-view", resourceModel, eventBusModel));
        add(new UploadResourceModal("upload-resource-modal", eventBusModel));
        add(new AddFolderModal("add-folder-modal", eventBusModel));
        add(new MoveResourceModal("move-resource-modal", allFolders, eventBusModel));
    }


    /**
     * When the folder changes, set the selected resource to the new folder.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onFolderChange(FolderChangeEvent event) {
        this.currentResource = null;
    }


    @Subscribe
    public void onResourceRename(UpdateClickedEvent event) {
        //TODO
    }


    /**
     * Delete the selected resource.
     * 
     * @param event
     *            AJAX event
     * @throws ROSRSException
     *             deleting the resource caused an unexpected response
     */
    @Subscribe
    public void onDeleteResourceClicked(ResourceDeleteClickedEvent event)
            throws ROSRSException {
        if (currentResource != null) {
            currentResource.delete();
            if (currentResource != currentFolder) {
                currentResource = currentFolder;
            } else {
                List<Folder> list = folderHierarchyModel.getObject();
                list.remove(currentFolder);
                if (list.isEmpty()) {
                    changeFolder(null, event.getTarget());
                } else {
                    changeFolder(list.get(list.size() - 1), event.getTarget());
                }
            }
        }
        eventBusModel.getObject().post(new ResourceDeletedEvent(event.getTarget()));
    }


    /**
     * Move a resource to a folder.
     * 
     * @param event
     *            trigger, with the destination folder
     * @throws ROSRSException
     *             deleting and adding the folder entry caused an unexpected response
     * @throws ROException
     *             when the data returned by ROSRS are incorrect
     */
    @Subscribe
    public void onResourceMove(ResourceMoveEvent event)
            throws ROSRSException, ROException {
        if (currentFolder != null) {
            FolderEntry entry = null;
            for (FolderEntry e : currentFolder.getFolderEntries().values()) {
                if (e.getResource().equals(currentResource)) {
                    entry = e;
                    break;
                }
            }
            if (entry != null) {
                entry.delete();
            }
        }
        if (!event.getFolder().isLoaded()) {
            event.getFolder().load(false);
        }
        event.getFolder().addEntry(currentResource, null);
        eventBusModel.getObject().post(new ResourceMovedEvent(event.getTarget()));
    }


    @Subscribe
    public void onResourceDuplicate(DuplicateEvent event) {
        //TODO
    }


    @Subscribe
    public void onResourceAddLink(AddLinkEvent event) {
        //TODO
    }


    /**
     * Aggregate a resource.
     * 
     * @param event
     *            AJAX event
     * 
     * @throws ROSRSException
     *             unexpected response code
     * @throws IOException
     *             can't get the uploaded file
     * @throws ROException
     *             the manifest is incorrect
     */
    @Subscribe
    public void onResourceAdd(ResourceAddReadyEvent event)
            throws ROSRSException, IOException, ROException {
        ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
        Resource resource;
        if (event.getUploadedFile() != null) {
            resource = researchObject.aggregate(event.getUploadedFile().getClientFileName(), event.getUploadedFile()
                    .getInputStream(), event.getUploadedFile().getContentType());
        } else {
            URI absoluteResourceURI = researchObject.getUri().resolve(event.getResourceUri());
            resource = researchObject.aggregate(absoluteResourceURI);
        }
        if (event.getResourceClass() != null) {
            resource.createPropertyValue(URI.create(RDF.type.getURI()), event.getResourceClass().getUri());
        }
        if (currentFolder != null) {
            currentFolder.addEntry(resource, null);
        }
        eventBusModel.getObject().post(new ResourceAddedEvent(event.getTarget()));
    }


    /**
     * Aggregate a folder.
     * 
     * @param event
     *            AJAX event
     * @throws ROSRSException
     *             unexpected response code
     * @throws ROException
     *             the manifest is incorrect
     */
    @Subscribe
    public void onFolderAdd(FolderAddReadyEvent event)
            throws ROSRSException, ROException {
        if (currentFolder != null) {
            currentFolder.addSubFolder(event.getFolderName()).getResource();
        } else {
            ((ResearchObject) this.getDefaultModelObject()).createFolder(event.getFolderName());
        }
        eventBusModel.getObject().post(new FolderAddedEvent(event.getTarget()));
    }


    /**
     * Change the current folder, set the current resource to it and post events.
     * 
     * @param newFolder
     *            the new folder
     * @param target
     *            AJAX request target
     */
    protected void changeFolder(Folder newFolder, AjaxRequestTarget target) {
        currentFolder = newFolder;
        currentResource = null;
        FolderChangeEvent event2 = new FolderChangeEvent(target);
        eventBusModel.getObject().post(event2);
    }


    public Folder getCurrentFolder() {
        return currentFolder;
    }


    public void setCurrentFolder(Folder currentFolder) {
        this.currentFolder = currentFolder;
    }


    public Resource getCurrentResource() {
        return currentResource;
    }


    public void setCurrentResource(Resource currentResource) {
        this.currentResource = currentResource;
    }

}
