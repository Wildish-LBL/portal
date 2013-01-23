package pl.psnc.dl.wf4ever.portal.pages.ro.components.folderviewer;

import javax.swing.tree.TreeModel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Basic RO files and folders viewer
 * 
 * @author pejot
 * 
 */
public class FoldersViewer extends Panel {

    /** makes wicket happy. */
    private static final long serialVersionUID = 1L;

    /** tree component. */
    private RoTree roTree;
    Fragment roTreeLoading;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            tree model
     */
    public FoldersViewer(String id, IModel<? extends TreeModel> model, MarkupContainer parent) {
        super(id);
        roTreeLoading = new Fragment("tree-test", "tmpRoTree", parent);
        roTree = new RoTree("ro-tree", model);
        add(roTree);
    }


    public void rrTest() {
        roTreeLoading.replaceWith(roTree);
    }
}
