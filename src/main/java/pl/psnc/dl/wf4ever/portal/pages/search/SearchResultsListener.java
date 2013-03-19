package pl.psnc.dl.wf4ever.portal.pages.search;

import org.purl.wf4ever.rosrs.client.search.dataclasses.SearchResult;

public interface SearchResultsListener {

    void onSearchResultsAvailable(SearchResult searchResult);

}
