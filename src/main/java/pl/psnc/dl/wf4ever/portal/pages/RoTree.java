/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import javax.swing.tree.TreeModel;

import org.apache.wicket.extensions.markup.html.tree.Tree;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

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


	public RoTree(String id, TreeModel model)
	{
		super(id, model);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected ResourceReference getCSS()
	{
		return CSS;
	}

}
