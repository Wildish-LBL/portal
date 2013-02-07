package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.behaviours;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;

/**
 * Load some content of the RO page in the background.
 * 
 * @author pejot
 * 
 */
public class ROExplorerAjaxInitialBehaviour extends AbstractDefaultAjaxBehavior {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ROExplorerAjaxInitialBehaviour.class);

    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** Research object. */
    private ResearchObject researchObject;
    /** Loadable Component. */
    private RoPage loadableComponent;


    /**
     * Constructor.
     * 
     * @param researchObject
     *            Research Object (tree root)
     * @param foldersViewer
     *            Component that will relad the data and will be refreshed.
     */
    public ROExplorerAjaxInitialBehaviour(ResearchObject researchObject, RoPage roPage) {
        super();
        this.researchObject = researchObject;
        this.loadableComponent = roPage;
    }


    @Override
    protected void respond(AjaxRequestTarget target) {
        //execute only one time to replace loading circle by ro tree
        if (loadableComponent.getRoTreeModel() != null) {
            return;
        }
        try {
            researchObject.load();
        } catch (ROSRSException | ROException e) {
            LOG.error(e.getMessage(), e);
        }
        RoTreeModel treeModel = null;
        treeModel = new RoTreeModel(researchObject);
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
        for (Folder folder : researchObject.getRootFolders()) {
            DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(folder);
            rootNode.add(currentNode);
            addNodeFolder(currentNode);
        }
        loadableComponent.onLoaded(treeModel);
        target.add(loadableComponent.getRoExplorerParent());
    }


    /**
     * Recursive method for filling up the tree.
     * 
     * @param parent
     *            parent tree node
     * 
     */
    private void addNodeFolder(DefaultMutableTreeNode parent) {
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


    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        super.renderHead(component, response);
        response.renderOnDomReadyJavaScript(getCallbackScript().toString());
    }

}
