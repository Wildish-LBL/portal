package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.search.SearchResult;

import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;

/**
 * A {@link PropertyListView} of search results.
 * 
 * @author piotrekhol
 * 
 */
final class SimpleSearchResultsListView extends PageableListView<SearchResult> {

    /** id. */
    private static final long serialVersionUID = 915182420617753899L;

    public static final int RESULTS_PER_PAGE = 5;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            model of search results
     */
    SimpleSearchResultsListView(String id, List<? extends SearchResult> model) {
        super(id, model, RESULTS_PER_PAGE);
    }


    @Override
    protected void populateItem(ListItem<SearchResult> item) {
        final SearchResult result = item.getModelObject();
        BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
        link.getPageParameters().add("ro", result.getResearchObject().getUri().toString());
        link.add(new Label("researchObject.name", new PropertyModel<String>(result, "researchObject.name")));
        item.add(link);
        item.add(new Label("researchObject.title", new PropertyModel<String>(result, "researchObject.title")));
        //            item.add(new CreatorsPanel("researchObject.creator", new PropertyModel<List<Creator>>(result,
        //                    "researchObject.creators")));
        item.add(new Label("researchObject.createdFormatted", new PropertyModel<String>(result,
                "researchObject.createdFormatted")));
        WebMarkupContainer score = new WebMarkupContainer("searchScore");
        item.add(score);
        score.setVisible(result.getScore() >= 0);
        score.add(new Label("scoreInPercent", new PropertyModel<String>(result, "scoreInPercent")));
        Label bar = new Label("percentBar", "");
        bar.add(new Behavior() {

            /** id. */
            private static final long serialVersionUID = -5409800651205755103L;


            @Override
            public void onComponentTag(final Component component, final ComponentTag tag) {
                super.onComponentTag(component, tag);
                tag.put("style", "width: " + Math.min(100, result.getScoreInPercent()) + "%");
            }
        });
        score.add(bar);
    }
}