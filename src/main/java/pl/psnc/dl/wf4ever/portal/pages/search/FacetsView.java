package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.FacetEntry;

/**
 * A list of facets.
 * 
 * @author piotrekhol
 * 
 */
public class FacetsView extends ListView<FacetEntry> {

    /** id. */
    private static final long serialVersionUID = -7767129758537264701L;

    /** selected facet values, for all facets. */
    private List<FacetValue> selected;


    /**
     * Constructor.
     * 
     * @param id
     *            markup id
     * @param selected
     *            selected facet values, for all facets
     * @param model
     *            model for all facets
     */
    public FacetsView(String id, List<FacetValue> selected, IModel<? extends List<? extends FacetEntry>> model) {
        super(id, model);
        this.selected = selected;
    }


    @Override
    //TODO it would be good to cut the HTML out from SearchResultsPage.html
    protected void populateItem(ListItem<FacetEntry> item) {
        FacetEntry facet = item.getModelObject();
        item.add(new Label("name", new PropertyModel<String>(facet, "name")));
        FacetValueView facetValueView = new FacetValueView("options", selected, new PropertyModel<List<FacetValue>>(
                item.getModel(), "values"));
        item.add(facetValueView);
        item.setVisible(facetValueView.hasVisibleValues());

    }

}
