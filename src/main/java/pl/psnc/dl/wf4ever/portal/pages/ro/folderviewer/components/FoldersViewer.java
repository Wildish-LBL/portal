package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components;

import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.ITreeStateListener;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;
import pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.bahaviours.FolderViewerAjaxInitialBahaviour;
import pl.psnc.dl.wf4ever.portal.ui.bahaviours.Loadable;
import pl.psnc.dl.wf4ever.portal.ui.components.LoadingCircle;

/**
 * Basic RO files and folders viewer
 * 
 * @author pejot
 * 
 */
public class FoldersViewer extends Panel implements Loadable, ITreeStateListener {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(FoldersViewer.class);

    /** Makes wicket happy. */
    private static final long serialVersionUID = 1L;

    /** Tree component. */
    private RoTree roTree;

    /** Loading image. */
    private LoadingCircle loadingCircle;

    /** Research object. */
    private ResearchObject researchObject;

    /** Loading information. */
    private static final String LOADING_OBJECT = "Loading Research Object metadata.<br />Please wait...";

    /** The model of RO resource. */
    private RoTreeModel roTreeModel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            tree model
     */
    public FoldersViewer(String id, ResearchObject researchObject) {
        super(id);
        //setting variables
        this.researchObject = researchObject;
        //building UI
        roTree = new RoTree("ro-tree", new PropertyModel<TreeModel>(this, "roTreeModel"));
        roTree.getTreeState().addTreeStateListener(this);
        loadingCircle = new LoadingCircle("ro-tree", LOADING_OBJECT);
        add(loadingCircle);
        //init in the background
        add(new FolderViewerAjaxInitialBahaviour(researchObject, this));
    }


    @Override
    public void onLoaded(Object data) {
        setRoTreeModel((RoTreeModel) data);
        loadingCircle.replaceWith(roTree);
    }


    public RoTreeModel getRoTreeModel() {
        return roTreeModel;
    }


    public void setRoTreeModel(RoTreeModel roTreeModel) {
        this.roTreeModel = roTreeModel;
    }


    @Override
    public void nodeSelected(Object node) {
    }


    @Override
    public void nodeUnselected(Object node) {
    }


    @Override
    public void allNodesCollapsed() {
    }


    @Override
    public void allNodesExpanded() {
    }


    @Override
    public void nodeCollapsed(Object node) {
    }


    @Override
    public void nodeExpanded(Object node) {
    }

}
