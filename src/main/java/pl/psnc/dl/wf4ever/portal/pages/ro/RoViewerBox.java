package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.crypt.Base64;

import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.RoTree;
import pl.psnc.dl.wf4ever.portal.services.RoFactory;

@SuppressWarnings("serial")
class RoViewerBox extends WebMarkupContainer {

    private static final Logger log = Logger.getLogger(RoViewerBox.class);

    /**
	 * 
	 */
    private final RoPage roPage;

    private RoTree conceptualTree;

    private RoTree physicalTree;

    private WebMarkupContainer interactiveView;

    Panel infoPanel;

    private final ItemInfoPanel itemInfo;

    private final InfoPanel resourceGroupInfo;

    final WebMarkupContainer actionButtons;

    private Fragment conceptualTreeLoading;

    private Fragment physicalTreeLoading;

    private Fragment interactiveViewLoading;

    final AbstractDefaultAjaxBehavior interactiveViewCallback;

    private MyAjaxButton deleteResource;

    private MyAjaxButton addResource;

    private Object selectedItem;

    private AjaxButton downloadROMetadata;

    private MyAjaxButton editResource;

    private ExternalLink downloadResource;

    private ExternalLink downloadROZipped;

    private transient String json;

    /** http://www.colourlovers.com/palette/1473/Ocean_Five */
    public static String[] interactiveViewColors = { "#00A0B0", "#6A4A3C", "#CC333F", "#EB6841", "#EDC951" };


    public RoViewerBox(final RoPage roPage, final CompoundPropertyModel<AggregatedResource> itemModel,
            IModel<? extends TreeModel> conceptualTreeModel, PropertyModel<TreeModel> physicalTreeModel,
            String tempRoTreeId) {
        super("roViewerBox", itemModel);
        this.roPage = roPage;
        setOutputMarkupId(true);

        final CompoundPropertyModel<ResourceGroup> resourceGroupModel = new CompoundPropertyModel<ResourceGroup>(
                (ResourceGroup) null);
        itemInfo = new ItemInfoPanel("itemInfo", itemModel);
        resourceGroupInfo = new InfoPanel("itemInfo", resourceGroupModel);
        infoPanel = itemInfo;
        add(infoPanel);

        conceptualTreeLoading = new Fragment("treeTable", tempRoTreeId, this.roPage);
        conceptualTree = new RoTree("treeTable", conceptualTreeModel) {

            @Override
            protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node) {
                Object object = ((DefaultMutableTreeNode) node).getUserObject();
                if (conceptualTree.getTreeState().isNodeSelected(node)) {
                    onResourceSelected(itemModel, resourceGroupModel, target, object);
                } else {
                    onResourceDeselected(itemModel, resourceGroupModel, target, object);
                }
            }

        };
        add(conceptualTreeLoading);

        physicalTreeLoading = new Fragment("physicalTreeTable", tempRoTreeId, this.roPage);
        physicalTree = new RoTree("physicalTreeTable", physicalTreeModel) {

            @Override
            protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node) {
                Object object = ((DefaultMutableTreeNode) node).getUserObject();
                if (physicalTree.getTreeState().isNodeSelected(node)) {
                    onResourceSelected(itemModel, resourceGroupModel, target, object);
                } else {
                    onResourceDeselected(itemModel, resourceGroupModel, target, object);
                }
            }

        };
        add(physicalTreeLoading);

        interactiveViewLoading = new Fragment("interactiveView", tempRoTreeId, this.roPage);
        interactiveView = new WebMarkupContainer("interactiveView");
        add(interactiveViewLoading);

        Form<?> roForm = new Form<Void>("roForm");
        add(roForm);

        addResource = new MyAjaxButton("addResource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                target.appendJavaScript("$('#upload-resource-modal').modal('show')");
            }

        };

        editResource = new MyAjaxButton("editResource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                //				target.appendJavaScript("$('#upload-resource-modal').modal('show')");
            }

        };

        deleteResource = new MyAjaxButton("deleteResource", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if (selectedItem instanceof AggregatedResource) {
                    try {
                        roPage.onResourceDelete((AggregatedResource) selectedItem, target);
                        conceptualTree.invalidateAll();
                        physicalTree.invalidateAll();
                        target.add(RoViewerBox.this);
                    } catch (Exception e) {
                        error(e);
                    }
                }
                target.add(roPage.getFeedbackPanel());
            }

        };
        downloadResource = new ExternalLink("downloadResource", new PropertyModel<String>(this,
                "selectedItem.downloadURI"));
        downloadResource.setBeforeDisabledLink(null);
        downloadResource.setAfterDisabledLink(null);

        downloadROZipped = new ExternalLink("downloadROZipped", new PropertyModel<String>(roPage, "ROZipLink"));
        downloadROMetadata = new MyAjaxButton("downloadMetadata", roForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                target.appendJavaScript("$('#download-metadata-modal').modal('show')");
            }

        };

        actionButtons = new WebMarkupContainer("actionButtons") {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                boolean nonROResource = selectedItem instanceof AggregatedResource
                        && !(selectedItem instanceof ResearchObject);
                addResource.setEnabled(roPage.canEdit);
                editResource.setEnabled(roPage.canEdit && nonROResource);
                deleteResource.setEnabled(roPage.canEdit && nonROResource);
                downloadResource.setEnabled(nonROResource
                        && ((AggregatedResource) selectedItem).getDownloadURI() != null);
            }
        };
        actionButtons.setOutputMarkupId(true);
        actionButtons.add(addResource);
        actionButtons.add(editResource);
        actionButtons.add(deleteResource);
        actionButtons.add(downloadResource);
        actionButtons.add(downloadROZipped);
        actionButtons.add(downloadROMetadata);
        roForm.add(actionButtons);

        interactiveViewCallback = new AbstractDefaultAjaxBehavior() {

            protected void respond(final AjaxRequestTarget target) {
                String nodeId = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("id").toString();
                boolean selected = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("selected")
                        .toBoolean(false);
                try {
                    URI resourceURI = new URI(new String(Base64.decodeBase64(nodeId)));
                    if (RoViewerBox.this.roPage.resources.containsKey(resourceURI)) {
                        if (selected) {
                            onResourceSelected(itemModel, resourceGroupModel, target,
                                RoViewerBox.this.roPage.resources.get(resourceURI));
                        } else {
                            onResourceDeselected(itemModel, resourceGroupModel, target,
                                RoViewerBox.this.roPage.resources.get(resourceURI));
                        }
                    }
                } catch (URISyntaxException e) {
                    log.error("Could not parse node id", e);
                }
                target.add(roPage.getFeedbackPanel());
            }
        };
        add(interactiveViewCallback);
    }


    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        add(new AbstractDefaultAjaxBehavior() {

            private static final long serialVersionUID = 2750059373726239613L;


            @Override
            protected void respond(AjaxRequestTarget target) {
                try {
                    if (roPage.resources != null && json == null) {
                        renderJSComponents(roPage.resources, target);
                    }
                } catch (IOException e) {
                    log.error(e);
                }
                target.add(roPage.getFeedbackPanel());
            }


            @Override
            public void renderHead(final Component component, final IHeaderResponse response) {
                super.renderHead(component, response);
                response.renderOnDomReadyJavaScript(getCallbackScript().toString());
            }

        });
    }


    public CharSequence getInteractiveViewCallbackUrl() {
        return interactiveViewCallback.getCallbackUrl();
    }


    private void setInfoPanel(final CompoundPropertyModel<AggregatedResource> itemModel, AggregatedResource res) {
        itemModel.setObject(res);
        if (infoPanel != itemInfo) {
            infoPanel.replaceWith(itemInfo);
            infoPanel = itemInfo;
        }
    }


    private void setInfoPanel(final CompoundPropertyModel<ResourceGroup> resourceGroupModel, ResourceGroup res) {
        resourceGroupModel.setObject(res);
        if (infoPanel != resourceGroupInfo) {
            infoPanel.replaceWith(resourceGroupInfo);
            infoPanel = resourceGroupInfo;
        }
    }


    public void onRoTreeLoaded() {
        conceptualTreeLoading.replaceWith(conceptualTree);
        physicalTreeLoading.replaceWith(physicalTree);
        interactiveViewLoading.replaceWith(interactiveView);
    }


    private void onResourceSelected(final CompoundPropertyModel<AggregatedResource> itemModel,
            final CompoundPropertyModel<ResourceGroup> resourceGroupModel, AjaxRequestTarget target, Object item) {
        this.selectedItem = item;
        if (item instanceof AggregatedResource) {
            setInfoPanel(itemModel, (AggregatedResource) item);
        } else if (item instanceof ResourceGroup) {
            setInfoPanel(resourceGroupModel, (ResourceGroup) item);
        }
        roPage.onResourceSelected(target);
        target.add(actionButtons);
        target.add(infoPanel);
    }


    private void onResourceDeselected(CompoundPropertyModel<AggregatedResource> itemModel,
            CompoundPropertyModel<ResourceGroup> resourceGroupModel, AjaxRequestTarget target, Object object) {
        this.selectedItem = null;
        setInfoPanel(itemModel, (AggregatedResource) null);
        roPage.onResourceSelected(target);
        target.add(actionButtons);
        target.add(infoPanel);
    }


    public void renderJSComponents(Map<URI, AggregatedResource> resources, AjaxRequestTarget target)
            throws IOException {
        json = RoFactory.createRoJSON(resources, interactiveViewColors);
        String callback = getInteractiveViewCallbackUrl().toString();
        target.appendJavaScript("var json = " + json + "; init(json, '" + callback + "');");
    }


    public RoTree getConceptualTree() {
        return conceptualTree;
    }


    public RoTree getPhysicalTree() {
        return physicalTree;
    }


    public Object getSelectedItem() {
        return selectedItem;
    }


    public void setSelectedItem(Object selectedItem) {
        this.selectedItem = selectedItem;
    }

}