/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.Thing;

/**
 * Tree model used for RO resources visualization.
 * 
 * @author piotrhol
 * 
 */
public class RoTreeModel extends DefaultTreeModel {

    /** id. */
    private static final long serialVersionUID = 4708607931110844599L;


    /**
     * Constructor.
     * 
     * @param root
     *            the Research Object
     */
    public RoTreeModel(Thing root) {
        super(new DefaultMutableTreeNode(root));
    }


    /**
     * Adds the resource grouping it to all matching folders. If no folders is matched, it is added directly to root
     * node.
     * 
     * @param resource
     *            resource to add
     * @param addToGroups
     *            should it be added to groups that it belongs to
     */
    public void addAggregatedResource(Resource resource) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
        root.add(new DefaultMutableTreeNode(resource));
    }


    /**
     * Removes all nodes that have this resource as object.
     * 
     * @param resource
     *            resource to remove
     */
    public void removeAggregatedResource(Resource resource) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
        @SuppressWarnings("unchecked")
        Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (resource.equals(node.getUserObject())) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                if (parent != null && parent.getChildCount() == 1) {
                    node.removeFromParent();
                    parent.removeFromParent();
                } else {
                    node.removeFromParent();
                }
            }
        }

    }
}
