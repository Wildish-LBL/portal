package pl.psnc.dl.wf4ever.portal.pages.base.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationIncrementLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;

public class BootstrapPagingNavigator extends AjaxPagingNavigator {

    /** id. */
    private static final long serialVersionUID = -5495224054362925121L;


    public BootstrapPagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }


    @Override
    protected Link<?> newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        return new Wrapper(id, new BootstrapPagingNavigationLink(id + "Inside", pageable, pageNumber), pageable);
    }


    @Override
    protected Link<?> newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        return new WrapperIncrement(id, new BootstrapPagingNavigationIncrementLink(id + "Inside", pageable, increment),
                pageable);
    }


    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        return new BootstrapPagingNavigation(id, pageable, labelProvider);
    }


    class Wrapper extends AjaxPagingNavigationLink {

        /** id. */
        private static final long serialVersionUID = -527364568167079132L;

        private AjaxPagingNavigationLink link;


        public Wrapper(String id, AjaxPagingNavigationLink link, IPageable pageable) {
            super(id, pageable, link.getPageNumber());
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


        @Override
        public void onClick() {
            link.onClick();
        }


        @Override
        public void onClick(AjaxRequestTarget target) {
            link.onClick(target);
        }

    }


    class WrapperIncrement extends AjaxPagingNavigationLink {

        /** id. */
        private static final long serialVersionUID = -527364568167079132L;

        private AjaxPagingNavigationIncrementLink link;


        public WrapperIncrement(String id, AjaxPagingNavigationIncrementLink link, IPageable pageable) {
            super(id, pageable, link.getPageNumber());
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


        @Override
        public void onClick() {
            link.onClick();
        }


        @Override
        public void onClick(AjaxRequestTarget target) {
            link.onClick(target);
        }

    }

}
