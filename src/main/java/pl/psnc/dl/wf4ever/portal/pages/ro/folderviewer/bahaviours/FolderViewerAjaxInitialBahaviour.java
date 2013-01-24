package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.bahaviours;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.purl.wf4ever.rosrs.client.ROException;
import org.purl.wf4ever.rosrs.client.ROSRSException;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;
import pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components.FoldersViewer;

/**
 * Load some content of the RO page in the background.
 * 
 * @author pejot
 * 
 */
public class FolderViewerAjaxInitialBahaviour extends AbstractDefaultAjaxBehavior {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(FolderViewerAjaxInitialBahaviour.class);

    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** Research object. */
    private ResearchObject researchObject;
    /** Loadable Component. */
    private FoldersViewer loadableComponent;


    /**
     * Constructor.
     * 
     * @param researchObject
     *            Research Object (tree root)
     * @param foldersViewer
     *            Component that will relad the data and will be refreshed.
     */
    public FolderViewerAjaxInitialBahaviour(ResearchObject researchObject, FoldersViewer foldersViewer) {
        super();
        this.researchObject = researchObject;
        this.loadableComponent = foldersViewer;
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

        RoTreeModel treeModel = new RoTreeModel(researchObject);
        for (Resource resource : researchObject.getResources().values()) {
            treeModel.addAggregatedResource(resource);
        }
        loadableComponent.onLoaded(treeModel);
        target.add(loadableComponent);
    }


    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        super.renderHead(component, response);
        response.renderOnDomReadyJavaScript(getCallbackScript().toString());
    }

}
