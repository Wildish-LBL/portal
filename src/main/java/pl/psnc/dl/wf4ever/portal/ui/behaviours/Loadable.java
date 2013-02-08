package pl.psnc.dl.wf4ever.portal.ui.behaviours;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * The interface for objects that can be loaded in the background.
 */
public interface Loadable {

    /**
     * Once the object is loaded.
     */
    void onLoaded(Object data, AjaxRequestTarget target);
}
