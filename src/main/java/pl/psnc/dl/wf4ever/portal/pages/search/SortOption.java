package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.Serializable;

import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.SearchServer.SortOrder;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.FacetEntry;

/**
 * A sort option - a facet plus the sort order.
 * 
 * @author piotrekhol
 * 
 */
public class SortOption implements Serializable {

    /** id. */
    private static final long serialVersionUID = -7469548685436393112L;

    /** Facet to sort by. */
    private final FacetEntry facetEntry;

    /** Sort order - descending or ascending. */
    private final SearchServer.SortOrder sortOrder;


    /**
     * Constructor.
     * 
     * @param facetEntry
     *            facet to sort by
     * @param sortOrder
     *            sort order
     */
    public SortOption(FacetEntry facetEntry, SortOrder sortOrder) {
        this.facetEntry = facetEntry;
        this.sortOrder = sortOrder;
    }


    public FacetEntry getFacetEntry() {
        return facetEntry;
    }


    public SearchServer.SortOrder getSortOrder() {
        return sortOrder;
    }


    /**
     * The key for the drop down list.
     * 
     * @return the facet name and the sort order
     */
    public String getKey() {
        return facetEntry.getFieldName() + "_" + sortOrder.toString();
    }


    /**
     * The name on the drop down list option.
     * 
     * @return the facet name and the sort order
     */
    public String getValue() {
        return facetEntry.getName() + ", " + (sortOrder == SortOrder.ASC ? "ascending" : "descending");
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getKey() == null) ? 0 : getKey().hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SortOption other = (SortOption) obj;
        if (getKey() == null) {
            if (other.getKey() != null) {
                return false;
            }
        } else if (!getKey().equals(other.getKey())) {
            return false;
        }
        return true;
    }
}
