package pl.psnc.dl.wf4ever.portal.events;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;

/**
 * User clicked Cancel.
 * 
 * @author piotrekhol
 * 
 */
public class FacetValueClickedEvent extends AbstractClickAjaxEvent {

    /** Facet value that is clicked. */
    private final FacetValue facetValue;


    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param facetValue
     *            Facet value that is clicked
     */
    public FacetValueClickedEvent(AjaxRequestTarget target, FacetValue facetValue) {
        super(target);
        this.facetValue = facetValue;
    }


    public FacetValue getFacetValue() {
        return facetValue;
    }
}
