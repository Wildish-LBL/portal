package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.FolderEntry;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.components.FolderBreadcrumbsPanel;
import pl.psnc.dl.wf4ever.portal.components.FolderContentsPanel;
import pl.psnc.dl.wf4ever.portal.components.annotations.AdvancedAnnotationsPanel;
import pl.psnc.dl.wf4ever.portal.components.annotations.CommentsList;
import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeleteClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeletedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMoveClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMoveEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMovedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceUpdateReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.UpdateClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.SketchEvent;
import pl.psnc.dl.wf4ever.portal.modals.AddFolderModal;
import pl.psnc.dl.wf4ever.portal.modals.MoveResourceModal;
import pl.psnc.dl.wf4ever.portal.modals.UpdateResourceModal;
import pl.psnc.dl.wf4ever.portal.modals.UploadResourceModal;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;
import pl.psnc.dl.wf4ever.portal.model.wicket.FolderHierarchyModel;
import pl.psnc.dl.wf4ever.portal.model.wicket.ResourceTypeModel;
import pl.psnc.dl.wf4ever.portal.services.CreateROThread;

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
public class RoContentPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(RoContentPanel.class);

    /** The currently viewed folder. */
    private Folder currentFolder;

    /** The currently selected resource (can be a folder). */
    private Resource currentResource;

    /** A list of visited folders. */
    private FolderHierarchyModel folderHierarchyModel;

    /** Modal window for adding resources. */
    private UploadResourceModal uploadResourceModal;

    /** Modal window for adding folders. */
    private AddFolderModal addFolderModal;

    /** Modal window for moving resources. */
    private MoveResourceModal moveResourceModal;

    /** Modal window for updating resources. */
    private UpdateResourceModal updateResourceModal;

    /** MIME type guessing utility. */
    private static MimetypesFileTypeMap mfm = new MimetypesFileTypeMap();

    static {
        try (InputStream mimeTypesIs = CreateROThread.class.getClassLoader().getResourceAsStream("mime.types")) {
            mfm = new MimetypesFileTypeMap(mimeTypesIs);
        } catch (IOException e) {
            LOG.error("Can't initialize mime types", e);
        }
    }


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            research object model
     */
    public RoContentPanel(String id, final IModel<ResearchObject> model) {
        super(id, model);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        IModel<Folder> folderModel = new PropertyModel<Folder>(this, "currentFolder");
        IModel<Resource> resourceModel = new PropertyModel<Resource>(this, "currentResource");
        IModel<List<Resource>> unrootedResourcesModel = new PropertyModel<List<Resource>>(getDefaultModel(),
                "resourcesWithoutFolders");
        IModel<List<Folder>> rootFolders = new PropertyModel<List<Folder>>(getDefaultModel(), "rootFolders");
        IModel<List<Folder>> allFolders = new PropertyModel<List<Folder>>(getDefaultModel(), "allFolders");
        folderHierarchyModel = new FolderHierarchyModel(folderModel);

        add(new FolderBreadcrumbsPanel("folder-breadcrumbs", folderHierarchyModel, folderModel));
        add(new FolderActionsPanel("folder-actions", folderModel));
        add(new FolderContentsPanel("folder-contents", folderModel, resourceModel, rootFolders, unrootedResourcesModel));
        add(new ResourceActionsPanel("resource-actions", resourceModel));
        add(new ResourceSummaryPanel("resource-summary", resourceModel, folderModel, allFolders));
        add(new CommentsList("resource-comments", resourceModel));
        add(new AdvancedAnnotationsPanel("advanced-annotations", "resource-basic-view", resourceModel));

        uploadResourceModal = new UploadResourceModal("upload-resource-modal");
        add(uploadResourceModal);
        addFolderModal = new AddFolderModal("add-folder-modal");
        add(addFolderModal);
        moveResourceModal = new MoveResourceModal("move-resource-modal", allFolders);
        add(moveResourceModal);
        updateResourceModal = new UpdateResourceModal("update-resource-modal");
        add(updateResourceModal);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof ResourceAddClickedEvent) {
            onAddResourceClicked((ResourceAddClickedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof FolderAddClickedEvent) {
            onAddFolderClicked((FolderAddClickedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof ResourceMoveClickedEvent) {
            onResourceMoveClicked((ResourceMoveClickedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof FolderChangeEvent) {
            onFolderChange((FolderChangeEvent) event.getPayload());
        }
        if (event.getPayload() instanceof UpdateClickedEvent) {
            onResourceUpdateClicked((UpdateClickedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof ResourceDeleteClickedEvent) {
            try {
                onDeleteResourceClicked((ResourceDeleteClickedEvent) event.getPayload());
            } catch (ROSRSException e) {
                error("Can't delete the resource: " + e.getMessage());
                LOG.error("Can't delete the resource", e);
            }
        }
        if (event.getPayload() instanceof ResourceMoveEvent) {
            try {
                onResourceMove((ResourceMoveEvent) event.getPayload());
            } catch (ROSRSException | ROException e) {
                error("Can't move the resource: " + e.getMessage());
                LOG.error("Can't move the resource", e);
            }
        }
        if (event.getPayload() instanceof ResourceAddReadyEvent) {
            try {
                onResourceAdd((ResourceAddReadyEvent) event.getPayload());
            } catch (ROSRSException | IOException | ROException e) {
                error("Can't add the resource: " + e.getMessage());
                LOG.error("Can't add the resource", e);
            }
        }
        if (event.getPayload() instanceof ResourceUpdateReadyEvent) {
            try {
                onResourceUpdate((ResourceUpdateReadyEvent) event.getPayload());
            } catch (ROSRSException | IOException | ROException e) {
                error("Can't update the resource: " + e.getMessage());
                LOG.error("Can't update the resource", e);
            }
        }
        if (event.getPayload() instanceof FolderAddReadyEvent) {
            try {
                onFolderAdd((FolderAddReadyEvent) event.getPayload());
            } catch (ROSRSException | ROException e) {
                error("Can't add the folder: " + e.getMessage());
                LOG.error("Can't add the folder", e);
            }
        }
    }


    /**
     * Show the modal.
     * 
     * @param event
     *            AJAX event
     */
    private void onAddResourceClicked(ResourceAddClickedEvent event) {
        UploadResourceModal uploadResourceModal2 = new UploadResourceModal("upload-resource-modal");
        uploadResourceModal.replaceWith(uploadResourceModal2);
        uploadResourceModal = uploadResourceModal2;
        uploadResourceModal.show(event.getTarget());
    }


    /**
     * Show the modal.
     * 
     * @param event
     *            AJAX event
     */
    private void onAddFolderClicked(FolderAddClickedEvent event) {
        AddFolderModal addFolderModal2 = new AddFolderModal("add-folder-modal");
        addFolderModal.replaceWith(addFolderModal2);
        addFolderModal = addFolderModal2;
        addFolderModal.show(event.getTarget());
    }


    /**
     * Show the modal.
     * 
     * @param event
     *            AJAX event
     */
    private void onResourceMoveClicked(ResourceMoveClickedEvent event) {
        IModel<List<Folder>> allFolders = new PropertyModel<List<Folder>>(getDefaultModel(), "allFolders");
        MoveResourceModal moveResourceModal2 = new MoveResourceModal("move-resource-modal", allFolders);
        moveResourceModal.replaceWith(moveResourceModal2);
        moveResourceModal = moveResourceModal2;
        moveResourceModal.show(event.getTarget());
    }


    /**
     * When the folder changes, set the selected resource to the new folder.
     * 
     * @param event
     *            AJAX event
     */
    private void onFolderChange(FolderChangeEvent event) {
        this.currentResource = null;
    }


    /**
     * Show the modal for updating the resource.
     * 
     * @param event
     *            AJAX event
     */
    private void onResourceUpdateClicked(UpdateClickedEvent event) {
        UpdateResourceModal updateResourceModal2 = new UpdateResourceModal("update-resource-modal");
        updateResourceModal.replaceWith(updateResourceModal2);
        updateResourceModal = updateResourceModal2;
        updateResourceModal.show(event.getTarget());
    }


    /**
     * Delete the selected resource.
     * 
     * @param event
     *            AJAX event
     * @throws ROSRSException
     *             deleting the resource caused an unexpected response
     */
    private void onDeleteResourceClicked(ResourceDeleteClickedEvent event)
            throws ROSRSException {
        if (currentResource != null) {
            boolean isSketch = updateSketch(event);
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
            if (isSketch)
                send(getPage(), Broadcast.BREADTH, new SketchEvent(event.getTarget()));
        }
        send(getPage(), Broadcast.BREADTH, new ResourceDeletedEvent(event.getTarget()));
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
    private void onResourceMove(ResourceMoveEvent event)
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
            event.getFolder().load();
        }
        event.getFolder().addEntry(currentResource, null);
        send(getPage(), Broadcast.BREADTH, new ResourceMovedEvent(event.getTarget()));
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
    private void onResourceAdd(ResourceAddReadyEvent event)
            throws ROSRSException, IOException, ROException {
        ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
        Resource resource;
        if (event.getUploadedFile() != null) {
            String contentType = event.getMimeType();
            if (contentType == null) {
                contentType = event.getUploadedFile().getContentType();
            }
            if (contentType == null || MediaType.APPLICATION_OCTET_STREAM.equals(contentType)) {
                contentType = mfm.getContentType(event.getUploadedFile().getClientFileName());
            }
            long sizeRes = -1;
            if (event.getUploadedFile().getSize() > 0)
                sizeRes = event.getUploadedFile().getSize();
            resource = researchObject.aggregate(event.getUploadedFile().getClientFileName(), event.getUploadedFile()
                    .getInputStream(), contentType, sizeRes);
        } else {
            URI absoluteResourceURI = researchObject.getUri().resolve(event.getResourceUri());
            resource = researchObject.aggregate(absoluteResourceURI);
        }
        if ("application/vnd.wf4ever.robundle+zip".equals(event.getMimeType())) {
            // Adding RO bundles may create additional annotations in the RO
            researchObject.load();
        }
        if (event.getResourceTypes() != null && !event.getResourceTypes().isEmpty()) {
            ResourceTypeModel resourceTypeModel = new ResourceTypeModel(new Model<Resource>(resource));
            resourceTypeModel.setObject(event.getResourceTypes());
            if (event.getResourceTypes().contains(ResourceType.SKETCH)) //LOG.debug("es sketch  "+this.getParent() + " and "+this.getParent().get("ro-summary") + " and page "+getPage() +" and target: "+event.getTarget());
                send(getPage(), Broadcast.BREADTH, new SketchEvent(event.getTarget()));

        }
        if (currentFolder != null) {
            currentFolder.addEntry(resource, null);
        }
        send(getPage(), Broadcast.BREADTH, new ResourceAddedEvent(event.getTarget()));
    }


    /**
     * Update a resource.
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
    private void onResourceUpdate(ResourceUpdateReadyEvent event)
            throws ROSRSException, IOException, ROException {
        if (event.getUploadedFile() != null) {
            long sizeRes = -1;
            if (event.getUploadedFile().getSize() > 0)
                sizeRes = event.getUploadedFile().getSize();
            currentResource.update(event.getUploadedFile().getInputStream(), event.getUploadedFile().getContentType(),
                sizeRes);
            boolean isSketch = updateSketch(event);
            if (isSketch)
                send(getPage(), Broadcast.BREADTH, new SketchEvent(event.getTarget()));
        }
        send(getPage(), Broadcast.BREADTH, new ResourceAddedEvent(event.getTarget()));
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
    private void onFolderAdd(FolderAddReadyEvent event)
            throws ROSRSException, ROException {
        if (currentFolder != null) {
            currentFolder.addSubFolder(event.getFolderName()).getResource();
        } else {
            ((ResearchObject) this.getDefaultModelObject()).createFolder(event.getFolderName());
        }
        send(getPage(), Broadcast.BREADTH, new FolderAddedEvent(event.getTarget()));
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
        send(getPage(), Broadcast.BREADTH, new FolderChangeEvent(target));
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


    private boolean updateSketch(AbstractAjaxEvent event) {
        boolean isSketch = false;
        List<AnnotationTriple> typeTriples = currentResource.getPropertyValues(RDF.type, false);
        for (AnnotationTriple triple : typeTriples) {
            try {
                URI typeUri = new URI(triple.getValue());
                if (typeUri.equals(ResourceType.SKETCH.getUri())) {
                    isSketch = true;
                    break;
                }
            } catch (URISyntaxException e) {
                LOG.warn("A type is not a URI: " + e.getMessage());
            }
        }
        //LOG.debug("resource: "+currentResource.getUri()+" is sketch: "+isSketch);
        return isSketch;

    }

}
