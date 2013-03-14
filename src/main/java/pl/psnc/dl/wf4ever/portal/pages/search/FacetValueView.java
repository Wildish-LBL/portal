package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;

import pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.behaviours.IAjaxLinkListener;

public class FacetValueView extends ListView<FacetValue> {

    /** id. */
    private static final long serialVersionUID = 4432740922914437268L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(FacetValueView.class);
    private List<FacetValue> selected;
    private Set<IAjaxLinkListener> listeners = new HashSet<>();


    public FacetValueView(String id, List<FacetValue> selected, IModel<? extends List<? extends FacetValue>> model) {
        super(id, model);
        this.selected = selected;
        this.setOutputMarkupId(true);
    }


    @Override
    protected void populateItem(final ListItem<FacetValue> item) {
        final FacetValue facetValue = item.getModelObject();
        final AjaxLink<Void> link = new AjaxLink<Void>("link") {

            /** id. */
            private static final long serialVersionUID = 8456829392076370486L;


            @Override
            public void onClick(AjaxRequestTarget target) {
                if (selected.contains(facetValue)) {
                    item.add(new SimpleAttributeModifier("class", ""));
                } else {
                    item.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                }
                target.add(item);
                for (IAjaxLinkListener listener : listeners) {
                    listener.onAjaxLinkClicked(facetValue, target);
                }
            }
        };
        link.add(new Label("label", new PropertyModel<String>(item.getModel(), "label")));
        link.add(new Label("count", new PropertyModel<String>(item.getModel(), "count")));
        item.add(link);

        item.setOutputMarkupId(true);
        link.setOutputMarkupId(true);

        if (facetValue.getCount() == 0) {
            item.setVisible(false);
        }
        for (FacetValue val : selected) {
            if (val.equals(facetValue)) {
                item.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                item.setVisible(true);
                break;
            }
        }
    }


    public boolean hasVisible() {
        for (FacetValue value : getList()) {
            if (value.getCount() > 0) {
                return true;
            }
            for (FacetValue selectedVal : selected) {
                if (selectedVal.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }


    public Set<IAjaxLinkListener> getListeners() {
        return listeners;
    }
}
