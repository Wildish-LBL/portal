package pl.psnc.dl.wf4ever.portal.pages.base.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigationIncrementLink;

public class BootstrapPagingNavigationIncrementLink extends PagingNavigationIncrementLink<Void> {

    /** id. */
    private static final long serialVersionUID = -7116715462343351839L;


    public BootstrapPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        super(id, pageable, increment);
    }


    @Override
    protected void disableLink(final ComponentTag tag) {
        // Remove any href from the old link
        tag.remove("href");

        tag.remove("onclick");

        //        tag.put("disabled", "disabled");
    }
}
