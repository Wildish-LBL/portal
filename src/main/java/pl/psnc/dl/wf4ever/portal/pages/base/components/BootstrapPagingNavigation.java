package pl.psnc.dl.wf4ever.portal.pages.base.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;

public class BootstrapPagingNavigation extends PagingNavigation {

    /** id. */
    private static final long serialVersionUID = -5495224054362925121L;


    public BootstrapPagingNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }


    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageIndex) {
        return new BootstrapPagingNavigationLink(id, pageable, pageIndex);
    }


    @Override
    protected LoopItem newItem(int iteration) {
        return new BootstrapLoopItem(iteration);
    }


    class BootstrapLoopItem extends LoopItem {

        /** id. */
        private static final long serialVersionUID = 7332178569152953133L;


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
