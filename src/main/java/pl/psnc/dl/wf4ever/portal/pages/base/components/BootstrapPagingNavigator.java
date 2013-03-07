package pl.psnc.dl.wf4ever.portal.pages.base.components;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

public class BootstrapPagingNavigator extends PagingNavigator {

    /** id. */
    private static final long serialVersionUID = -5495224054362925121L;


    public BootstrapPagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }


    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        return new BootstrapPagingNavigationLink(id, pageable, pageNumber);
    }


    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        return new BootstrapPagingNavigationIncrementLink(id, pageable, increment);
    }


    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        return new BootstrapPagingNavigation(id, pageable, labelProvider);
    }

}
