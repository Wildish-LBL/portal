package pl.psnc.dl.wf4ever.portal.events.search;

import pl.psnc.dl.wf4ever.portal.pages.search.SortOption;

/**
 * Called when the selected sort option has changed.
 * 
 * @author piotrekhol
 * 
 */
public class SortOptionChangeEvent {

    /** the new sort option. */
    private final SortOption newSortOption;


    /**
     * Constructor.
     * 
     * @param newSortOption
     *            the new sort option
     */
    public SortOptionChangeEvent(SortOption newSortOption) {
        this.newSortOption = newSortOption;
    }


    public SortOption getNewSortOption() {
        return newSortOption;
    }

}
