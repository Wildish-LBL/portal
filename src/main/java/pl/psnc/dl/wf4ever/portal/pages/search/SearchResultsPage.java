package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.portal.model.SearchResult;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
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
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(SearchResultsPage.class);


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     */
    public SearchResultsPage(final List<SearchResult> searchResults) {
        super(null);
        setDefaultModel(new CompoundPropertyModel<SearchResultsPage>(this));

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
        searchResultsDiv.setOutputMarkupId(true);
        add(searchResultsDiv);

        final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
        searchResultsDiv.add(noResults);
        noResults.setVisible(searchResults == null || searchResults.isEmpty());

        ListView<SearchResult> searchResultsList = new SearchResultsListView("searchResultsListView", searchResults);
        searchResultsList.setReuseItems(true);
        searchResultsDiv.add(searchResultsList);
    }

}
