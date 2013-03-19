package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.repeater.AbstractPageableView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FoundRO;

public class LazySearchResultsView extends AbstractPageableView<FoundRO> {

    /** id. */
    private static final long serialVersionUID = -3507871650130245421L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(LazySearchResultsView.class);
    private SearchServer searchServer;
    private String query;


    public LazySearchResultsView(String id, SearchServer searchServer, String query, int resultsPerPage) {
        super(id);
        this.searchServer = searchServer;
        this.query = query;
        setItemsPerPage(resultsPerPage);
    }


    @Override
    protected Iterator<IModel<FoundRO>> getItemModels(int offset, int size) {
        try {
            List<FoundRO> results = searchServer.search(query, offset, size, null).getROsList();
            return new ModelIterator<>(results);
        } catch (SearchException e) {
            LOGGER.error("Can't search more data", e);
            return null;
        }
    }


    @Override
    protected int internalGetItemCount() {
        return 50;
    }


    @Override
    protected void populateItem(Item<FoundRO> item) {
        SearchResultsPage.populateItem(item);
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

        private final Iterator<? extends T> items;
        private final int max;
        private int index;


        /**
         * Constructor
         * 
         * @param dataProvider
         *            data provider
         * @param offset
         *            index of first item
         * @param count
         *            max number of items to return
         */
        public ModelIterator(List<T> items) {
            this.items = items.iterator();
            max = items.size();
        }


        /**
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }


        /**
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return items != null && items.hasNext() && (index < max);
        }


        /**
         * @see java.util.Iterator#next()
         */
        public IModel<T> next() {
            index++;
            return new Model<T>(items.next());
        }
    }

}
