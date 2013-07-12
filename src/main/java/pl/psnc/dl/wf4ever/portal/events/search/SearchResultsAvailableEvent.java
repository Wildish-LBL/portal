package pl.psnc.dl.wf4ever.portal.events.search;

import org.purl.wf4ever.rosrs.client.search.dataclasses.SearchResult;

/**
 * Search results have become ready to display.
 * 
 * @author piotrekhol
 * 
 */
public class SearchResultsAvailableEvent {

    /** search results. */
    private final SearchResult searchResult;


    /**
     * Constructor.
     * 
     * @param result
     *            search results
     */
    public SearchResultsAvailableEvent(SearchResult result) {
        this.searchResult = result;
    }


    public SearchResult getSearchResult() {
        return searchResult;
    }
}
