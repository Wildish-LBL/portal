package pl.psnc.dl.wf4ever.portal.pages.base.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigationLink;

public class BootstrapPagingNavigationLink extends PagingNavigationLink<Void> {

    /** id. */
    private static final long serialVersionUID = -6475077411075979233L;


    public BootstrapPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        super(id, pageable, pageNumber);
    }


    @Override
    protected void disableLink(final ComponentTag tag) {
        // Remove any href from the old link
        tag.remove("href");

        tag.remove("onclick");

        //        tag.put("disabled", "disabled");
    }

}
