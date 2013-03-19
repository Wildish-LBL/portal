package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.List;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FoundRO;

/**
 * A {@link PageableListView} of search results.
 * 
 * @author piotrekhol
 * 
 */
final class SimpleSearchResultsView extends PageableListView<FoundRO> {

    /** id. */
    private static final long serialVersionUID = 915182420617753899L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            model of search results
     * @param resultsPerPage
     *            maximum number of results per page
     */
    SimpleSearchResultsView(String id, List<? extends FoundRO> model, int resultsPerPage) {
        super(id, model, resultsPerPage);
    }


    @Override
    protected void populateItem(ListItem<FoundRO> item) {
        SearchResultsPage.populateItem(item);
    }

}
