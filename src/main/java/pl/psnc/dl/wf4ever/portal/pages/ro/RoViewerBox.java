package pl.psnc.dl.wf4ever.portal.pages.ro;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.RoTree;

/**
 * The part of RO page that displays the RO structure.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
class RoViewerBox extends WebMarkupContainer {

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RoViewerBox.class);

    /** The owning page. */
    private final RoPage roPage;

    /** Physical RO resources tree. */
    private RoTree physicalTree;

    /** The abstract resource information panel. */
    Panel infoPanel;

    /** The aggregated resource information panel. */
    private final ItemInfoPanel itemInfo;

    /** The resource group information panel. */
    private final InfoPanel resourceGroupInfo;

    /** The RO/resource manipulation buttons. */
    final WebMarkupContainer actionButtons;

    /** A spinning circle. */
    private Fragment physicalTreeLoading;

    /** Delete resource button. */
    private MyAjaxButton deleteResource;

    /** Add resource button. */
    private MyAjaxButton addResource;

    /** Selected item - resource, RO, resource group. */
    private Object selectedItem;

    /** Download RO metadata button. */
    private AjaxButton downloadROMetadata;

    /** Edit resource button. */
    private MyAjaxButton editResource;

    /** Download resource button. */
    private ExternalLink downloadResource;

    /** Download RO ZIP button. */
    private ExternalLink downloadROZipped;


    /**
     * Constructor.
     * 
     * @param roPage
     *            the owning page
     * @param itemModel
     *            the RO model
     * @param conceptualTreeModel
     *            the conceptual tree model
     * @param physicalTreeModel
     *            the physical tree model
     * @param tempRoTreeId
     *            the spinning circle wicket id
     */
    public RoViewerBox(final RoPage roPage, final CompoundPropertyModel<AggregatedResource> itemModel,
            PropertyModel<TreeModel> physicalTreeModel, String tempRoTreeId) {
        super("roViewerBox", itemModel);
        this.roPage = roPage;
        setOutputMarkupId(true);

        final CompoundPropertyModel<ResourceGroup> resourceGroupModel = new CompoundPropertyModel<ResourceGroup>(
                (ResourceGroup) null);
        itemInfo = new ItemInfoPanel("itemInfo", itemModel);
        resourceGroupInfo = new InfoPanel("itemInfo", resourceGroupModel);
        infoPanel = itemInfo;
        add(infoPanel);

        physicalTreeLoading = new Fragment("physicalTreeTable", tempRoTreeId, this.roPage);
        physicalTree = new RoTree("physicalTreeTable", physicalTreeModel) {

            @Override
            protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node) {
                Object object = ((DefaultMutableTreeNode) node).getUserObject();
                if (physicalTree.getTreeState().isNodeSelected(node)) {
                    onResourceSelected(itemModel, resourceGroupModel, target, object);
                } else {
                    onResourceDeselected(itemModel, resourceGroupModel, target);
                }
            }

        };
        add(physicalTreeLoading);

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
                if (selectedItem instanceof Resource) {
                    try {
                        roPage.onResourceDelete((Resource) selectedItem, target);
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
        downloadResource.setBeforeDisabledLink("");
        downloadResource.setAfterDisabledLink("");

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
    }


    /**
     * Set the info panel to an aggregated resource panel.
     * 
     * @param itemModel
     *            selected resource model
     * @param res
     *            selected resource
     */
    private void setInfoPanel(final CompoundPropertyModel<AggregatedResource> itemModel, AggregatedResource res) {
        itemModel.setObject(res);
        if (infoPanel != itemInfo) {
            infoPanel.replaceWith(itemInfo);
            infoPanel = itemInfo;
        }
    }


    /**
     * Set the info panel to a resource group panel.
     * 
     * @param resourceGroupModel
     *            selected resource group model
     * @param res
     *            selected resource group
     */
    private void setInfoPanel(final CompoundPropertyModel<ResourceGroup> resourceGroupModel, ResourceGroup res) {
        resourceGroupModel.setObject(res);
        if (infoPanel != resourceGroupInfo) {
            infoPanel.replaceWith(resourceGroupInfo);
            infoPanel = resourceGroupInfo;
        }
    }


    /**
     * Update the RO structure visualizations.
     */
    public void onRoTreeLoaded() {
        physicalTreeLoading.replaceWith(physicalTree);
    }


    /**
     * Update the panel when a resource is selected by the user.
     * 
     * @param itemModel
     *            selected resource model
     * @param resourceGroupModel
     *            selected resource group model
     * @param target
     *            request target
     * @param item
     *            selected resource/resource group
     */
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


    /**
     * Update the panel when a resource is deselected by the user.
     * 
     * @param itemModel
     *            selected resource model
     * @param resourceGroupModel
     *            selected resource group model
     * @param target
     *            request target
     */
    private void onResourceDeselected(CompoundPropertyModel<AggregatedResource> itemModel,
            CompoundPropertyModel<ResourceGroup> resourceGroupModel, AjaxRequestTarget target) {
        this.selectedItem = null;
        setInfoPanel(itemModel, (AggregatedResource) null);
        roPage.onResourceSelected(target);
        target.add(actionButtons);
        target.add(infoPanel);
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
