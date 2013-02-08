/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.Thing;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

/**
 * Tree model used for RO resources visualization.
 * 
 * @author piotrhol
 * 
 */
public class RoTreeModel extends DefaultTreeModel {

    /** id. */
    private static final long serialVersionUID = 4708607931110844599L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(RoTreeModel.class);


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
     * @return mpde
     */
    public DefaultMutableTreeNode addAggregatedResource(Resource resource) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(resource);
        root.add(node);
        return node;
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


    public static RoTreeModel create(ResearchObject researchObject) {
        RoTreeModel treeModel = new RoTreeModel(researchObject);
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        for (Folder folder : researchObject.getRootFolders()) {
            DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(folder);
            rootNode.add(currentNode);
            addNodeFolder(currentNode);
        }
        return treeModel;
    }


    /**
     * Recursive method for filling up the tree.
     * 
     * @param parent
     *            parent tree node
     * 
     */
    private static void addNodeFolder(DefaultMutableTreeNode parent) {
        Folder currentFolder = (Folder) (parent.getUserObject());
        try {
            currentFolder.load(false);
        } catch (ROSRSException e) {
            LOG.error("Folder " + currentFolder.getUri().toString() + " can not be loaded", e);
        }
        for (Folder child : currentFolder.getSubfolders()) {
            DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(child);
            parent.add(currentNode);
            addNodeFolder(currentNode);
        }
    }
}
