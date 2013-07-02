package pl.psnc.dl.wf4ever.portal.components.pagination;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;

/**
 * An {@link AjaxPagingNavigation} compatible with the Bootstrap styling.
 * 
 * @author piotrekhol
 * 
 */
public class BootstrapPagingNavigation extends AjaxPagingNavigation {

    /** id. */
    private static final long serialVersionUID = -5495224054362925121L;


    /**
     * Constructor.
     * 
     * @param id
     *            See Component
     * @param pageable
     *            The underlying pageable component to navigate
     * @param labelProvider
     *            The label provider for the text that the links should be displaying.
     */
    public BootstrapPagingNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }


    @Override
    protected Link<?> newPagingNavigationLink(String id, IPageable pageable, int pageIndex) {
        return new BootstrapPagingNavigationLink(id, pageable, pageIndex);
    }


    @Override
    protected LoopItem newItem(int iteration) {
        return new BootstrapLoopItem(iteration);
    }


    /**
     * A {@link LoopItem} compatible with Bootstrap styling.
     * 
     * @author piotrekhol
     * 
     */
    class BootstrapLoopItem extends LoopItem {

        /** id. */
        private static final long serialVersionUID = 7332178569152953133L;


        /**
         * Constructor.
         * 
         * @param index
         *            The index of the item
         */
        public BootstrapLoopItem(int index) {
            super(index);
        }


        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            // Get the index of page this link shall point to
            final int pageIndex = getStartIndex() + getIndex();
            if (pageIndex == pageable.getCurrentPage()) {
                tag.put("class", "active");
            }
        }
    }

}
