package pl.psnc.dl.wf4ever.portal.events.aggregation;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;

/**
 * User wants to add a new folder.
 * 
 * @author piotrekhol
 * 
 */
public class FolderAddReadyEvent extends AbstractAjaxEvent {

    /** Folder name, relative to current folder. */
    private final String folderName;


    /**
     * Constructor for a file uploaded with content.
     * 
     * @param target
     *            response target
     * @param folderName
     *            folder name, relative to current folder
     */
    public FolderAddReadyEvent(AjaxRequestTarget target, String folderName) {
        super(target);
        this.folderName = folderName;
    }


    public String getFolderName() {
        return folderName;
    }
}
