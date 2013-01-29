package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components.behaviours;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Ajax Link listener interface.
 * 
 * @author pejot
 * 
 */
public interface IAjaxLinkListener {

    /**
     * On click event.
     * 
     * @param target
     */
    void onAjaxLinkOnClick(AjaxRequestTarget target);
}
