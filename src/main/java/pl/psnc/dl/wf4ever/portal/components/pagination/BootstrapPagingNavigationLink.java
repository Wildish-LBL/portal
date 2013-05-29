package pl.psnc.dl.wf4ever.portal.components.pagination;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.navigation.paging.IPageable;

public class BootstrapPagingNavigationLink extends AjaxPagingNavigationLink {

    /** id. */
    private static final long serialVersionUID = -6475077411075979233L;


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
