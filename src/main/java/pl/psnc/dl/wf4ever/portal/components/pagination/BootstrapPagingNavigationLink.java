package pl.psnc.dl.wf4ever.portal.components.pagination;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.navigation.paging.IPageable;

/**
 * An {@link AjaxPagingNavigationLink} compatible with the Bootstrap styling.
 * 
 * @author piotrekhol
 * 
 */
public class BootstrapPagingNavigationLink extends AjaxPagingNavigationLink {

    /** id. */
    private static final long serialVersionUID = -6475077411075979233L;


    /**
     * Constructor.
     * 
     * @param id
     *            See Component
     * @param pageable
     *            The pageable component for this page link
     * @param pageNumber
     *            The page number in the PageableListView that this link links to. Negative pageNumbers are relative to
     *            the end of the list.
     */
    public BootstrapPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        super(id, pageable, pageNumber);
    }


    @Override
    protected void disableLink(ComponentTag tag) {
        tag.remove("href");
        tag.remove("onclick");
        tag.put("class", "active");
    }

}
