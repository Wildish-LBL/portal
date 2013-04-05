package pl.psnc.dl.wf4ever.portal.listeners;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * Ajax Link listener interface.
 * 
 * @author pejot
 * 
 */
public interface IAjaxLinkListener extends Serializable {

    /**
     * On click event.
     * 
     * @param source
     *            the object that was clicked
     * @param target
     *            the Ajax target related to this event (add to it the components that should be refreshed)
     */
    void onAjaxLinkClicked(Object source, AjaxRequestTarget target);
}
