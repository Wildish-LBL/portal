package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchResult;
import org.purl.wf4ever.rosrs.client.search.SearchServer;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
import pl.psnc.dl.wf4ever.portal.pages.base.components.BootstrapPagingNavigator;
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

    /** Currently displayed search results. */
    private List<SearchResult> searchResults;


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

        try {
            searchResults = searchServer.search(searchKeywords);
        } catch (SearchException e) {
            error(e.getMessage());
            LOGGER.error("Can't do the search for " + searchKeywords, e);
        }

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
        searchResultsDiv.setOutputMarkupId(true);
        add(searchResultsDiv);

        final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
        searchResultsDiv.add(noResults);
        noResults.setVisible(searchResults == null || searchResults.isEmpty());

        SimpleSearchResultsListView searchResultsList = new SimpleSearchResultsListView("searchResultsListView",
                searchResults);
        searchResultsList.setReuseItems(true);
        searchResultsDiv.add(searchResultsList);

        add(new BootstrapPagingNavigator("pagination", searchResultsList));
    }


    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

}
