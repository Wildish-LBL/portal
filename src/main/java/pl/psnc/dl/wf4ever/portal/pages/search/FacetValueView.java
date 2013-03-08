package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.search.FacetValue;

import pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.behaviours.IAjaxLinkListener;

public class FacetValueView extends ListView<FacetValue> {

    /** id. */
    private static final long serialVersionUID = 4432740922914437268L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(FacetValueView.class);

    private Set<IAjaxLinkListener> listeners = new HashSet<>();


    public FacetValueView(String id, IModel<? extends List<? extends FacetValue>> model) {
        super(id, model);
    }


    @Override
    protected void populateItem(ListItem<FacetValue> item) {
        final FacetValue facetValue = item.getModelObject();
        AjaxLink<Void> link = new AjaxLink<Void>("link") {

            /** id. */
            private static final long serialVersionUID = 8456829392076370486L;


            @Override
            public void onClick(AjaxRequestTarget target) {
                for (IAjaxLinkListener listener : listeners) {
                    listener.onAjaxLinkClicked(facetValue, target);
                }
            }
        };
        link.add(new Label("label", new PropertyModel<String>(item.getModel(), "label")));
        link.add(new Label("count", new PropertyModel<String>(item.getModel(), "count")));
        item.add(link);
    }


    public Set<IAjaxLinkListener> getListeners() {
        return listeners;
    }
}
