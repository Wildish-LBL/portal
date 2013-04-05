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
     * @param target
     */
    void onAjaxLinkClicked(Object source, AjaxRequestTarget target);
}
