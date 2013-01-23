package pl.psnc.dl.wf4ever.portal.pages.ro.components.folderviewer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.model.IModel;
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

    /**
     * Reference to the css file.
     */
    private static final ResourceReference CSS = new PackageResourceReference(RoTree.class, "tree.css");


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
        /*
        add(new AbstractDefaultAjaxBehavior() {

           
            private static final long serialVersionUID = 1L;


            @Override
            protected void respond(AjaxRequestTarget target) {
                IRequestParameters params = RequestCycle.get().getRequest().getPostParameters();
            }


            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                System.out.println(getCallbackUrl().toString());
                JavaScriptResourceReference treeClassRefernce = new JavaScriptResourceReference(RoTree.class,
                        "roTree.js");
                response.renderJavaScriptReference(treeClassRefernce);
                response.renderOnDomReadyJavaScript("test_tree_reload(\"" + getCallbackUrl().toString() + "\");");
            }
        });
        */
    }


    @Override
    public RoTreeModel getModelObject() {
        return (RoTreeModel) super.getModelObject();
    }


    @Override
    protected ResourceReference getCSS() {
        return CSS;
    }


    /*
        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.renderOnDomReadyJavaScript("test_tree_reload();");
        }
    */

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

}
