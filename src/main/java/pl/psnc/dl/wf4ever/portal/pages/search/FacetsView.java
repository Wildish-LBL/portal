package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.search.Facet;
import org.purl.wf4ever.rosrs.client.search.FacetValue;

import pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.behaviours.IAjaxLinkListener;

public class FacetsView extends ListView<Facet> implements IAjaxLinkListener {

    /** id. */
    private static final long serialVersionUID = -7767129758537264701L;

    private Set<IAjaxLinkListener> listeners = new HashSet<>();


    public FacetsView(String id, IModel<? extends List<? extends Facet>> model) {
        super(id, model);
    }


    @Override
    //TODO it would be good to cut the HTML out from SearchResultsPage.html
    protected void populateItem(ListItem<Facet> item) {
        Facet facet = item.getModelObject();
        item.add(new Label("name", new PropertyModel<String>(facet, "name")));
        FacetValueView facetValueView = new FacetValueView("options", new PropertyModel<List<FacetValue>>(
                item.getModel(), "values"));
        facetValueView.getListeners().add(this);
        item.add(facetValueView);

    }


    public Set<IAjaxLinkListener> getListeners() {
        return listeners;
    }


    @Override
    public void onAjaxLinkClicked(Object source, AjaxRequestTarget target) {
        for (IAjaxLinkListener listener : listeners) {
            listener.onAjaxLinkClicked(source, target);
        }
    }

}
