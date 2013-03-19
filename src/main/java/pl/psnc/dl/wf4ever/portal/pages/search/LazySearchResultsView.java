package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.repeater.AbstractPageableView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.SearchServer.SortOrder;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FoundRO;
import org.purl.wf4ever.rosrs.client.search.dataclasses.SearchResult;

/**
 * A search results generator that fetches new results for each results page separately.
 * 
 * @author piotrekhol
 * 
 */
public class LazySearchResultsView extends AbstractPageableView<FoundRO> {

    /** id. */
    private static final long serialVersionUID = -3507871650130245421L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(LazySearchResultsView.class);

    /** model of the sort fields. */
    private IModel<Map<String, SortOrder>> sortFields;

    /** listeners for event of getting new search results. */
    private List<SearchResultsListener> listeners = new ArrayList<>();

    /** current offset. */
    private int offset = 0;
    /** Search results. */
    SearchResult results;


    /**
     * Constructor.
     * 
     * @param id
     *            markup id
     * @param searchServer
     *            search server from which the results are fetched
     * @param query
     *            query to send to the search server
     * @param resultsPerPage
     *            how many results should be fetched for each page
     * @param sortFieldsModel
     *            model of the sort fields
     */
    public LazySearchResultsView(String id, SearchServer searchServer, String query, int resultsPerPage,
            PropertyModel<Map<String, SortOrder>> sortFieldsModel) {
        super(id);
        this.sortFields = sortFieldsModel;
        setItemsPerPage(resultsPerPage);
        try {
            results = searchServer.search(query, offset, resultsPerPage, sortFields.getObject());
        } catch (SearchException e) {
            LOGGER.error("Can't search data", e);
        }
    }


    @Override
    protected Iterator<IModel<FoundRO>> getItemModels(int offset, int size) {
        if (results == null) {
            return null;
        }
        this.offset = offset;
        for (SearchResultsListener listener : listeners) {
            listener.onSearchResultsAvailable(results);
        }
        return new ModelIterator<>(results.getROsList());

    }


    @Override
    protected int internalGetItemCount() {
        Long l = results.getNumFound();
        return new Integer(l.toString());
    }


    @Override
    protected void populateItem(Item<FoundRO> item) {
        SearchResultsPage.populateItem(item, offset + item.getIndex() + 1);
    }


    public List<SearchResultsListener> getListeners() {
        return listeners;
    }


    /**
     * Helper class that converts input from IDataProvider to an iterator over view items.
     * 
     * @author Igor Vaynberg (ivaynberg)
     * 
     * @param <T>
     *            Model object type
     */
    private static final class ModelIterator<T extends Serializable> implements Iterator<IModel<T>> {

        /** items. */
        private final Iterator<? extends T> items;

        /** items count. */
        private final int max;

        /** index for the iterator. */
        private int index;


        /**
         * Constructor.
         * 
         * @param items
         *            items
         */
        public ModelIterator(List<T> items) {
            this.items = items.iterator();
            max = items.size();
        }


        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }


        @Override
        public boolean hasNext() {
            return items != null && items.hasNext() && (index < max);
        }


        @Override
        public IModel<T> next() {
            index++;
            return new Model<T>(items.next());
        }
    }

}
