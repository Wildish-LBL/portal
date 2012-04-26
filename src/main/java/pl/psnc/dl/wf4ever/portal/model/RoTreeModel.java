/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

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
	 * Adds the resource grouping it to all matching folders. If no folders is matched, it
	 * is added directly to root node.
	 * 
	 * @param resource
	 */
	public void addAggregatedResource(AggregatedResource resource, boolean addToGroups)
	{
		if (!addToGroups || resource.getMatchingGroups().isEmpty()) {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
			root.add(new DefaultMutableTreeNode(resource));
			return;
		}

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.getRoot();
		Set<ResourceGroup> notFound = new HashSet<>(resource.getMatchingGroups());
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = e.nextElement();
			if (node.getUserObject() instanceof ResourceGroup
					&& resource.getMatchingGroups().contains(node.getUserObject())) {
				node.add(new DefaultMutableTreeNode(resource));
				notFound.remove(node.getUserObject());
			}
		}

		for (ResourceGroup resourceGroup : notFound) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(resourceGroup);
			node.add(new DefaultMutableTreeNode(resource));
			root.insert(node, 0);
		}
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
