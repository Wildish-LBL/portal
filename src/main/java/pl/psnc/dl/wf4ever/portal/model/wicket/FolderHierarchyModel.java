package pl.psnc.dl.wf4ever.portal.model.wicket;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Folder;

/**
 * A complex read only model that maintains a stack of folders (the current location) based on the currently selected
 * folder.
 * 
 * @author piotrekhol
 * 
 */
public class FolderHierarchyModel implements IModel<List<Folder>> {

    /** id. */
    private static final long serialVersionUID = 8242326045833266222L;

    /** currently selected folder. */
    private IModel<Folder> folderModel;

    /** the folder stack representing the path from the root folder to the currently selected folder. */
    private List<Folder> list = new ArrayList<Folder>();


    /**
     * Constructor.
     * 
     * @param folderModel
     *            currently selected folder
     */
    public FolderHierarchyModel(IModel<Folder> folderModel) {
        this.folderModel = folderModel;
    }


    @Override
    public List<Folder> getObject() {
        if (list.isEmpty()) {
            if (folderModel.getObject() == null) {
                return list;
            } else {
                list.add(folderModel.getObject());
                return list;
            }
        } else {
            Folder last = list.get(list.size() - 1);
            if (folderModel.getObject() == null) {
                list.clear();
                return list;
            } else if (last.equals(folderModel.getObject())) {
                return list;
            } else if (last.getSubfolders().contains(folderModel.getObject())) {
                list.add(folderModel.getObject());
                return list;
            } else if (list.contains(folderModel.getObject())) {
                list = new ArrayList<>(list.subList(0, list.indexOf(folderModel.getObject()) + 1));
                return list;
            } else {
                list.clear();
                list.add(folderModel.getObject());
                return list;
            }
        }
    }


    @Override
    public void detach() {
    }


    @Override
    public void setObject(List<Folder> object) {
        list = object;
    }

}
