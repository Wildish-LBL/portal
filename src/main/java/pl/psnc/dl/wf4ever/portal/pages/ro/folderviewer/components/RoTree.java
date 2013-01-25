package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.purl.wf4ever.rosrs.client.Thing;

import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;

/**
 * An extended {@link Tree}.
 * 
 * @author piotrhol
 * 
 */
public class RoTree extends Tree {

    /** id. */
    private static final long serialVersionUID = 7615686756338665378L;

    /** Reference to the css file. */
    private static final ResourceReference CSS = new PackageResourceReference(RoTree.class, "tree.css");
    /** Reference to the image file. */
    private static final ResourceReference TREE_IMAGE = new PackageResourceReference(RoTree.class, "tree-images.png");
    /** Reference to the js file. */
    private static final JavaScriptResourceReference treeClassRefernce = new JavaScriptResourceReference(RoTree.class,
            "roTree.js");


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            tree model
     */
    public RoTree(String id, IModel<? extends TreeModel> model) {
        super(id, model);
        add(new AbstractDefaultAjaxBehavior() {

            private static final long serialVersionUID = 1L;


            @Override
            protected void respond(AjaxRequestTarget target) {
            }


            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);

                response.renderJavaScriptReference(treeClassRefernce);
                response.renderOnDomReadyJavaScript("test_tree_reload(\"" + getCallbackUrl().toString() + "\");");
            }
        });

    }


    @Override
    public RoTreeModel getModelObject() {
        return (RoTreeModel) super.getModelObject();
    }


    @Override
    protected ResourceReference getCSS() {
        return CSS;
    }


    @Override
    protected ResourceReference getNodeIcon(TreeNode node) {
        return super.getNodeIcon(node);
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        getTreeState().collapseAll();
        if (getModelObject() != null) {
            getTreeState().expandNode(getModelObject().getRoot());
        }
    }


    @Override
    protected String renderNode(TreeNode node) {
        Thing thing = (Thing) ((DefaultMutableTreeNode) node).getUserObject();
        return thing.getName();
    }


    @Override
    protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node) {
        Thing object = (Thing) ((DefaultMutableTreeNode) node).getUserObject();

        /*if (physicalTree.getTreeState().isNodeSelected(node)) {
            onResourceSelected(itemModel, target, object);
        } else {
            onResourceDeselected(itemModel, target);
        }
        */
    }
}
