package pl.psnc.dl.wf4ever.portal.pages.search;

/**
 * A listener to the changes in the selection sort option.
 * 
 * @author piotrekhol
 * 
 */
public interface SortOptionChangeListener {

    /**
     * Called when the selected sort option has changed.
     * 
     * @param newSortOption
     *            the new sort option
     */
    void onSortOptionChanged(SortOption newSortOption);

}
