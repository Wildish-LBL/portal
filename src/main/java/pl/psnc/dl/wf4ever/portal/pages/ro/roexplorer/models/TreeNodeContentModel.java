package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.Thing;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

/**
 * Model that returns list of resources for a selected node.
 * 
 * @author pejot
 * 
 */
public class TreeNodeContentModel implements IModel<List<Resource>> {

    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** Logger. */
    private static final Logger LOG = Logger.getLogger(TreeNodeContentModel.class);
    /** Selected resource model(respresented by sleected node). */
    private IModel<Thing> selectedResourceModel;
    /** Research object represented by tree. */
    private ResearchObject researchObject;


    /**
     * Constructor.
     * 
     * @param selectedResourceModel
     *            selected resource model
     * @param researchObject
     *            research object
     */
    public TreeNodeContentModel(IModel<Thing> selectedResourceModel, ResearchObject researchObject) {
        super();
        this.selectedResourceModel = selectedResourceModel;
        this.researchObject = researchObject;
    }


    @Override
    public List<Resource> getObject() {
        Thing selectedResource = selectedResourceModel.getObject();
        if (!researchObject.isLoaded()) {
            return null;
        }
        if (selectedResource == null) {
            return new ArrayList<Resource>(researchObject.getResources().values());
        } else {
            if (selectedResource instanceof ResearchObject) {
                try {
                    return researchObject.getResourcesWithoutFolders();
                } catch (ROSRSException e) {
                    LOG.error("Can not load resources without folders from " + researchObject, e);
                }
            } else if (selectedResource instanceof Folder) {
                return ((Folder) selectedResource).getResources();
            }
        }
        return null;
    }


    @Override
    public void detach() {

    }


    @Override
    public void setObject(List<Resource> object) {
        LOG.warn("TreeNodeContentModel doesn't allow to set objects");
    }
}
