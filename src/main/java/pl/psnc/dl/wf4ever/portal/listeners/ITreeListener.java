package pl.psnc.dl.wf4ever.portal.listeners;

import javax.swing.tree.TreeNode;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Tree events listener.
 * 
 * @author pejot
 * 
 */
public interface ITreeListener {

    /**
     * Click event listener.
     * 
     * @param target
     *            ajax target
     * @param node
     */
    void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node);
}
