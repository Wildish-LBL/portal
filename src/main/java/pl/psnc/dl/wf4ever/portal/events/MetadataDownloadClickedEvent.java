package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * User wants to download RO metadata.
 * 
 * @author piotrekhol
 * 
 */
public class MetadataDownloadClickedEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public MetadataDownloadClickedEvent(AjaxRequestTarget target) {
        super(target);
    }

}
