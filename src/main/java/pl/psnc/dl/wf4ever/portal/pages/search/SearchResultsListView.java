package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;

import pl.psnc.dl.wf4ever.portal.model.SearchResult;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;

/**
 * A {@link PropertyListView} of search results.
 * 
 * @author piotrekhol
 * 
 */
final class SearchResultsListView extends PropertyListView<SearchResult> {

    /** id. */
    private static final long serialVersionUID = 915182420617753899L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            model of search results
     */
    SearchResultsListView(String id, List<? extends SearchResult> model) {
        super(id, model);
    }


    @Override
    protected void populateItem(ListItem<SearchResult> item) {
        final SearchResult result = item.getModelObject();
        BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
        link.getPageParameters().add("ro", result.getResearchObject().getUri().toString());
        link.add(new Label("researchObject.name"));
        item.add(link);
        item.add(new Label("researchObject.title"));
        item.add(new Label("scoreInPercent"));
        //            item.add(new CreatorsPanel("researchObject.creator", new PropertyModel<List<Creator>>(result,
        //                    "researchObject.creators")));
        item.add(new Label("researchObject.createdFormatted"));
        Label bar = new Label("percentBar", "");
        bar.add(new Behavior() {

            /**
             * 
             */
            private static final long serialVersionUID = -5409800651205755103L;


            @Override
            public void onComponentTag(final Component component, final ComponentTag tag) {
                super.onComponentTag(component, tag);
                tag.put("style", "width: " + Math.min(100, result.getScoreInPercent()) + "%");
            }
        });
        item.add(bar);
    }
}