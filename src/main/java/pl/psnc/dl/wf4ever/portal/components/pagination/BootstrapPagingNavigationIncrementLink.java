package pl.psnc.dl.wf4ever.portal.components.pagination;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationIncrementLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.navigation.paging.IPageable;

/**
 * An {@link AjaxPagingNavigationIncrementLink} compatible with the Bootstrap styling.
 * 
 * @author piotrekhol
 * 
 */
public class BootstrapPagingNavigationIncrementLink extends AjaxPagingNavigationIncrementLink {

    /** id. */
    private static final long serialVersionUID = -7116715462343351839L;


    /**
     * Constructor.
     * 
     * @param id
     *            See Component
     * @param pageable
     *            The pageable component the page links are referring to
     * @param increment
     *            increment by
     */
    public BootstrapPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        super(id, pageable, increment);
    }


    @Override
    protected void disableLink(final ComponentTag tag) {
        tag.remove("href");
        tag.remove("onclick");
    }
}
