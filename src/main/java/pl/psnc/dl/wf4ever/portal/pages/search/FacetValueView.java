package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;

import pl.psnc.dl.wf4ever.portal.events.FacetValueClickedEvent;

/**
 * A view of options for a facet.
 * 
 * @author piotrekhol
 * 
 */
public class FacetValueView extends ListView<FacetValue> {

    /** id. */
    private static final long serialVersionUID = 4432740922914437268L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(FacetValueView.class);

    /** selected values for this facet. */
    private List<FacetValue> selected;


    /**
     * Constructor.
     * 
     * @param id
     *            markup id
     * @param selected
     *            selected values for this facet
     * @param model
     *            model for a list of available facet values
     */
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
                    item.add(AttributeAppender.replace("class", ""));
                } else {
                    item.add(AttributeAppender.replace("class", "selected_filter_label"));
                }
                target.add(item);
                send(getPage(), Broadcast.BREADTH, new FacetValueClickedEvent(target, facetValue));
            }
        };
        link.add(new Label("label", facetValue.getLabel()));
        link.add(new Label("count", "" + facetValue.getCount()));
        item.add(link);

        item.setOutputMarkupId(true);
        link.setOutputMarkupId(true);

        if (facetValue.getCount() == 0) {
            item.setVisible(false);
        }
        for (FacetValue val : selected) {
            if (val.equals(facetValue)) {
                item.add(AttributeAppender.replace("class", "selected_filter_label"));
                item.setVisible(true);
                break;
            }
        }
    }


    /**
     * Should this facet value be visible?
     * 
     * @return true if any value has a count greater than 0 or is selected, false otherwise
     */
    public boolean hasVisibleValues() {
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

}
