/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * @author piotrhol
 * 
 */
public class RoTreeModel
	extends DefaultTreeModel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4708607931110844599L;


	public RoTreeModel(TreeNode root)
	{
		super(root);
	}


	/**
	 * Adds the resource to the end of the list, not grouping it to any folder.
	 * 
	 * @param resource
	 */
	public void addAggregatedResource(AggregatedResource resource)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
		root.add(new DefaultMutableTreeNode(resource));
	}


	/**
	 * Removes all nodes that have this resource as object.
	 * 
	 * @param resource
	 */
	public void removeAggregatedResource(AggregatedResource resource)
	{
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (resource.equals(node.getUserObject())) {
				node.removeFromParent();
			}
		}

	}
}
