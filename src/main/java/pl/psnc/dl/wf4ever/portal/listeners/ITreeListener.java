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
     *            the Ajax target related to this event (add to it the components that should be refreshed)
     * @param node
     *            the tree node that was clicked
     */
    void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node);
}
