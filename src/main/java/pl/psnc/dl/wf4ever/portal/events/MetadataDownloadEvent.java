package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

/**
 * User wants to download metadata of the RO.
 * 
 * @author piotrekhol
 * 
 */
public class MetadataDownloadEvent extends AbstractAjaxEvent {

    /** The format of metadata selected by the user. */
    private final RDFFormat format;


    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param format
     *            the format of metadata selected by the user
     */
    public MetadataDownloadEvent(AjaxRequestTarget target, RDFFormat format) {
        super(target);
        this.format = format;
    }


    public RDFFormat getFormat() {
        return format;
    }

}
