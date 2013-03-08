package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchResult;
import org.purl.wf4ever.rosrs.client.search.SearchServer;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
import pl.psnc.dl.wf4ever.portal.pages.base.components.BootstrapPagingNavigator;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

/**
 * The home page.
 * 
 * @author piotrekhol
 * 
 */
public class SearchResultsPage extends Base {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchResultsPage.class);

    public static final int RESULTS_PER_PAGE = 10;


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     */
    public SearchResultsPage(final String searchKeywords) {
        super(new PageParameters());
        setDefaultModel(new CompoundPropertyModel<SearchResultsPage>(this));

        SearchServer searchServer = ((PortalApplication) getApplication()).getSearchServer();

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
        searchResultsDiv.setOutputMarkupId(true);
        add(searchResultsDiv);

        searchResultsDiv.add(new Label("searchKeywords", searchKeywords));

        IPageable searchResultsList = null;

        if (searchServer.supportsPagination()) {
            searchResultsList = new LazySearchResultsView("searchResultsListView", searchServer, searchKeywords,
                    RESULTS_PER_PAGE);
        } else {
            List<SearchResult> searchResults = null;
            try {
                searchResults = searchServer.search(searchKeywords);
            } catch (SearchException e) {
                error(e.getMessage());
                LOGGER.error("Can't do the search for " + searchKeywords, e);
            }
            searchResultsList = new SimpleSearchResultsView("searchResultsListView", searchResults, RESULTS_PER_PAGE);
        }
        searchResultsDiv.add((Component) searchResultsList);

        final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
        searchResultsDiv.add(noResults);
        //        noResults.setVisible(searchResults == null || searchResults.isEmpty());
        noResults.setVisible(false);

        add(new BootstrapPagingNavigator("pagination", searchResultsList));
    }


    public static void populateItem(ListItem<SearchResult> item) {
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
