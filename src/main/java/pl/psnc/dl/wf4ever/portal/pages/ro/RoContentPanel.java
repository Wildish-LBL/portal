package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.components.FolderBreadcrumbsPanel;
import pl.psnc.dl.wf4ever.portal.components.FolderContentsPanel;
import pl.psnc.dl.wf4ever.portal.components.annotations.CommentsList;
import pl.psnc.dl.wf4ever.portal.events.AddLinkEvent;
import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;
import pl.psnc.dl.wf4ever.portal.events.RoLoadedEvent;
import pl.psnc.dl.wf4ever.portal.events.ShowAllAnnotationsEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.DuplicateEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.MoveEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.RenameEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeleteClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeletedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotateEvent;
import pl.psnc.dl.wf4ever.portal.modals.AddFolderModal;
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
public class RoContentPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RoContentPanel.class);

    /** The currently viewed folder. */
    private Folder currentFolder;

    /** The currently selected resource (can be a folder). */
    private Resource currentResource;

    /** Event bus model for changes to the RO coming out of this panel. */
    private IModel<EventBus> eventBusModel;

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
        super(id, model);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        this.eventBusModel = eventBusModel;
        eventBusModel.getObject().register(this);

        IModel<Folder> folderModel = new PropertyModel<Folder>(this, "currentFolder");
        IModel<Resource> resourceModel = new PropertyModel<Resource>(this, "currentResource");
        IModel<List<Resource>> unrootedResourcesModel = new PropertyModel<List<Resource>>(getDefaultModel(),
                "resourcesWithoutFolders");
        folderHierarchyModel = new FolderHierarchyModel(folderModel);

        add(new FolderBreadcrumbsPanel("folder-breadcrumbs", folderHierarchyModel, folderModel, eventBusModel));
        add(new FolderActionsPanel("folder-actions", folderModel, eventBusModel));
        add(new FolderContentsPanel("folder-contents", folderModel, resourceModel, unrootedResourcesModel,
                eventBusModel));
        add(new ResourceActionsPanel("resource-actions", resourceModel, eventBusModel));
        add(new ResourceSummaryPanel("resource-summary", resourceModel, eventBusModel));
        add(new CommentsList("resource-comments", resourceModel, eventBusModel));
        add(new UploadResourceModal("upload-resource-modal", eventBusModel));
        add(new AddFolderModal("add-folder-modal", eventBusModel));
    }


    @Subscribe
    public void onAnnotateClicked(AnnotateEvent event) {
        //TODO
        System.out.println("annotate");
    }


    @Subscribe
    public void onShowAllAnnotationsClicked(ShowAllAnnotationsEvent event) {
        //TODO        
        System.out.println("show all");
    }


    /**
     * Initialize the current folder and resource when the RO metadata have been loaded.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onRoLoaded(RoLoadedEvent event) {
        Set<Folder> rootFolders = ((ResearchObject) getDefaultModelObject()).getRootFolders();
        if (rootFolders.isEmpty()) {
            changeFolder(null, event.getTarget());
        } else {
            // pick any
            changeFolder(rootFolders.iterator().next(), event.getTarget());
        }
        event.getTarget().add(this);
    }


    /**
     * When the folder changes, set the selected resource to the new folder.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onFolderChange(FolderChangeEvent event) {
        this.currentResource = currentFolder;
    }


    @Subscribe
    public void onResourceRename(RenameEvent event) {
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


    @Subscribe
    public void onResourceMove(MoveEvent event) {
        //TODO
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
            Folder folder = ((ResearchObject) this.getDefaultModelObject()).createFolder(event.getFolderName());
            changeFolder(folder, event.getTarget());
        }
        FolderAddedEvent event2 = new FolderAddedEvent(event.getTarget());
        eventBusModel.getObject().post(event2);
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
        currentResource = currentFolder;
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
