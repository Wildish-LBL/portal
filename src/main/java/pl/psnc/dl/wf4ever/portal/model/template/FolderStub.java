package pl.psnc.dl.wf4ever.portal.model.template;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * A template for a folder.
 * 
 * @author piotrekhol
 * 
 */
public class FolderStub implements Serializable {

    /** id. */
    private static final long serialVersionUID = -2174879390192546801L;

    /** folder path, relative to the RO. */
    private final String path;

    /** an unmodifiable set of folders that should go inside this folder. */
    private final Set<FolderStub> subfolders;


    /**
     * Constructor.
     * 
     * @param path
     *            folder path, relative to the RO
     * @param subfolders
     *            a set of folders that should go inside this folder
     */
    public FolderStub(String path, Set<FolderStub> subfolders) {
        this.path = path;
        this.subfolders = Collections.unmodifiableSet(subfolders);
    }


    public String getPath() {
        return path;
    }


    public Set<FolderStub> getSubfolders() {
        return subfolders;
    }

}
