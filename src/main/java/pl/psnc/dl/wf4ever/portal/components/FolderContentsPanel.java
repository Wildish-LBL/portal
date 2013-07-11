package pl.psnc.dl.wf4ever.portal.components;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.AggregationChangedEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Lists the resources inside a folder, plus all resources that are not in any folder.
 * 
 * @author piotrekhol
 * 
 */
public class FolderContentsPanel extends EventPanel {

    /**
     * Behavior for emphasizing graphically the resource if it's selected.
     * 
     * @author piotrekhol
     * 
     */
    private final class ResourceSelectedBehaviour extends Behavior {

        /** id. */
        private static final long serialVersionUID = -5842150372531742905L;

        /** The resource for which the behavior is performed. */
        private Resource resource;


        /**
         * Constructor.
         * 
         * @param resource
         *            The resource for which the behavior is performed
         */
        public ResourceSelectedBehaviour(Resource resource) {
            this.resource = resource;
        }


        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            super.onComponentTag(component, tag);
            if (resource.equals(resourceModel.getObject())) {
                tag.append("class", "info", " ");
            }
        }
    }


    /**
     * Behavior for creating an AJAX event when the resource is clicked.
     * 
     * @author piotrekhol
     * 
     */
    private final class ResourceClickedBehaviour extends AjaxEventBehavior {

        /** id. */
        private static final long serialVersionUID = -7851716059681985652L;

        /** The resource for which the behavior is performed. */
        private Resource resource;


        /**
         * Constructor.
         * 
         * @param resource
         *            the resource for which the behavior is performed
         * @param event
         *            the JavaScript event for which the selection should be triggered, i.e. "onclick"
         */
        private ResourceClickedBehaviour(Resource resource, String event) {
            super(event);
            this.resource = resource;
        }


        @Override
        protected void onEvent(AjaxRequestTarget target) {
            resourceModel.setObject(resource);
            eventBusModel.getObject().post(new ResourceSelectedEvent(target));
        }
    }


    /** id. */
    private static final long serialVersionUID = 6161074268125343983L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(FolderContentsPanel.class);

    /** A CSS file for this panel. */
    private static final CssResourceReference CSS_REFERENCE = new CssResourceReference(FolderContentsPanel.class,
            "FolderContentsPanel.css");

    /** Currently selected resource. */
    private IModel<Resource> resourceModel;


    /**
     * Constructor.
     * 
     * @param id
     *            Wicket ID
     * @param model
     *            current folder
     * @param resourceModel
     *            currently selected resource
     * @param rootFolders
     *            root folders
     * @param unrootedResourcesModel
     *            unrooted resources, will be displayed alongside root folders
     * @param eventBusModel
     *            event bus
     */
    public FolderContentsPanel(String id, final IModel<Folder> model, final IModel<Resource> resourceModel,
            final IModel<List<Folder>> rootFolders, final IModel<List<Resource>> unrootedResourcesModel,
            final IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupId(true);
        this.resourceModel = resourceModel;

        IModel<List<Folder>> foldersModel = new AbstractReadOnlyModel<List<Folder>>() {

            /** id. */
            private static final long serialVersionUID = -1522760963878243661L;


            @Override
            public List<Folder> getObject() {
                if (model.getObject() != null) {
                    return model.getObject().getSubfolders();
                } else {
                    return rootFolders.getObject();
                }
            }

        };
        add(new ListView<Folder>("folders", foldersModel) {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            protected void populateItem(ListItem<Folder> item) {
                final Folder folder = item.getModelObject();
                AjaxLink<String> link = new AjaxLink<String>("name-link", new PropertyModel<String>(folder, "name")) {

                    /** id. */
                    private static final long serialVersionUID = 3495158432497901769L;


                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        model.setObject(folder);
                        eventBusModel.getObject().post(new FolderChangeEvent(target));
                    }
                };
                item.add(link);
                link.add(new Label("name", new PropertyModel<String>(folder, "name")));
                item.add(new Label("comments-cnt", "" + folder.getPropertyValues(RDFS.comment, false).size()));
                item.add(new ResourceClickedBehaviour(folder, "onclick"));
                item.add(new ResourceSelectedBehaviour(folder));
            }

        });
        IModel<List<Resource>> resourcesModel = new AbstractReadOnlyModel<List<Resource>>() {

            /** id. */
            private static final long serialVersionUID = -1522760963878243661L;


            @Override
            public List<Resource> getObject() {
                if (model.getObject() != null) {
                    return model.getObject().getResources();
                } else {
                    return unrootedResourcesModel.getObject();
                }
            }

        };
        add(new ListView<Resource>("files", resourcesModel) {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            protected void populateItem(ListItem<Resource> item) {
                Resource resource = item.getModelObject();
                item.add(new Label("name", new PropertyModel<String>(resource, "name")));
                item.add(new Label("comments-cnt", "" + resource.getPropertyValues(RDFS.comment, false).size()));
                item.add(new ResourceClickedBehaviour(resource, "onclick"));
                item.add(new ResourceSelectedBehaviour(resource));
            }

        });
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS_REFERENCE));
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        try {
            if (this.getDefaultModelObject() != null) {
                ((Folder) this.getDefaultModelObject()).load(false);
            }
        } catch (ROSRSException e) {
            LOG.error("Can't load folder: " + this.getDefaultModelObjectAsString(), e);
        }
    }


    /**
     * Called when the current resource has changed.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onResourceSelected(ResourceSelectedEvent event) {
        event.getTarget().add(this);
    }


    /**
     * Called when the contents of the folder have changed.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAggregationChanged(AggregationChangedEvent event) {
        event.getTarget().add(this);
    }
}
