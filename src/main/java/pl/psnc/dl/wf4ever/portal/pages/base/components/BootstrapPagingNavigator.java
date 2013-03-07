package pl.psnc.dl.wf4ever.portal.pages.base.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
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
        return new Wrapper(id, new BootstrapPagingNavigationLink(id + "Inside", pageable, pageNumber));
    }


    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        return new Wrapper(id, new BootstrapPagingNavigationIncrementLink(id + "Inside", pageable, increment));
    }


    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        return new BootstrapPagingNavigation(id, pageable, labelProvider);
    }


    class Wrapper extends AbstractLink {

        /** id. */
        private static final long serialVersionUID = -527364568167079132L;

        private Link<Void> link;


        public Wrapper(String id, Link<Void> link) {
            super(id);
            this.link = link;
            add(link);
        }


        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (!link.isEnabled()) {
                tag.put("class", "disabled");
            }
        }

    }

}
