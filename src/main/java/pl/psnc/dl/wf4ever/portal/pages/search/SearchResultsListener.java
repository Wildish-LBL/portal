package pl.psnc.dl.wf4ever.portal.pages.search;

import org.purl.wf4ever.rosrs.client.search.dataclasses.SearchResult;

/**
 * Listener for the search results generator.
 * 
 * @author piotrekhol
 * 
 */
public interface SearchResultsListener {

    /**
     * Called when new results have been fetched from the server.
     * 
     * @param searchResult
     *            the new search results
     */
    void onSearchResultsAvailable(SearchResult searchResult);

}
