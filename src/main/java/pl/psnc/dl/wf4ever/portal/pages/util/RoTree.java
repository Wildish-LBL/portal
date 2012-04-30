/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.util;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;

/**
 * @author piotrhol
 * 
 */
public class RoTree
	extends Tree
{

	private static final long serialVersionUID = 7615686756338665378L;

	/**
	 * Reference to the css file.
	 */
	private static final ResourceReference CSS = new PackageResourceReference(RoTree.class, "res/tree.css");


	/** Reference to the icon of tree item (not a folder) */
	//	private static final ResourceReference WORKFLOW = new PackageResourceReference(RoTree.class, "res/workflow.png");
	//
	//	private static final ResourceReference EXTERNAL = new PackageResourceReference(RoTree.class, "res/external.png");

	public RoTree(String id, IModel< ? extends TreeModel> model)
	{
		super(id, model);
	}


	@Override
	public RoTreeModel getModelObject()
	{
		return (RoTreeModel) super.getModelObject();
	}


	@Override
	protected ResourceReference getCSS()
	{
		return CSS;
	}


	@Override
	protected ResourceReference getNodeIcon(TreeNode node)
	{
		//		Object object = ((DefaultMutableTreeNode) node).getUserObject();
		//		if (object instanceof AggregatedResource) {
		//			AggregatedResource res = (AggregatedResource) object;
		//			if (res.getType() == Type.WORKFLOW) {
		//				return WORKFLOW;
		//			}
		//			if (res.getType() == Type.WEB_SERVICE) {
		//				return EXTERNAL;
		//			}
		//		}
		return super.getNodeIcon(node);
	}


	@Override
	protected void onConfigure()
	{
		super.onConfigure();
		getTreeState().collapseAll();
		if (getModelObject() != null) {
			getTreeState().expandNode(getModelObject().getRoot());
			//			getTreeState().selectNode(getModelObject().getRoot(), true);
		}
	}

}
