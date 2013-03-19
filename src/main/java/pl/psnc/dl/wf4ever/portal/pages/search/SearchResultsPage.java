package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.SearchServer.SortOrder;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FacetValue;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FoundRO;
import org.purl.wf4ever.rosrs.client.search.dataclasses.SearchResult;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.FacetEntry;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
import pl.psnc.dl.wf4ever.portal.pages.base.components.BootstrapPagingNavigator;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.behaviours.IAjaxLinkListener;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

/**
 * The home page.
 * 
 * @author piotrekhol
 * 
 */
public class SearchResultsPage extends Base implements IAjaxLinkListener, SearchResultsListener {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchResultsPage.class);

    public static final int RESULTS_PER_PAGE = 15;

    private List<FacetEntry> facetsList = null;
    private List<FoundRO> rosList = null;
    private String searchKeywords = null;
    private List<FacetValue> selected = null;
    private List<FacetValue> savedSelected = null;
    private String originalKeywords = null;
    IPageable searchResultsList = null;

    private WebMarkupContainer searchResultsDiv;
    private AjaxLink<Object> clearFilters;

    private Map<String, SortOrder> sortMap;


    public SearchResultsPage() {
        this("", null, "", null);
    }


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     */
    public SearchResultsPage(final String searchKeywords, final List<FacetValue> selected,
            final String originalKeywords, Map<String, SortOrder> _sortMap) {
        super(new PageParameters());
        sortMap = _sortMap != null ? _sortMap : new HashMap<String, SortOrder>();
        this.savedSelected = new ArrayList<>();
        this.selected = new ArrayList<>();
        if (selected != null) {
            savedSelected.addAll(selected);
            this.selected = selected;
        }
        if (originalKeywords == null) {
            this.originalKeywords = searchKeywords;
        } else {
            this.originalKeywords = originalKeywords;
        }
        this.searchKeywords = searchKeywords;

        searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
        searchResultsDiv.setOutputMarkupId(true);
        searchResultsDiv.add(new Label("searchKeywords", originalKeywords));
        add(searchResultsDiv);
        setDefaultModel(new CompoundPropertyModel<SearchResultsPage>(this));
        SearchServer searchServer = ((PortalApplication) getApplication()).getSearchServer();

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        if (searchServer.supportsPagination()) {
            LazySearchResultsView lazySearchResultsList = new LazySearchResultsView("searchResultsListView",
                    searchServer, searchKeywords, RESULTS_PER_PAGE, sortMap);
            lazySearchResultsList.getListeners().add(this);
            searchResultsList = lazySearchResultsList;
        } else {
            SearchResult searchResult = null;

            Map<String, String> queryMap = new HashMap<>();
            for (FacetValue value : savedSelected) {
                if (queryMap.containsKey(value.getParamName())) {
                    String queryPart = queryMap.get(value.getParamName()) + " OR " + value.getQuery();
                    queryMap.put(value.getParamName(), queryPart);
                } else {
                    queryMap.put(value.getParamName(), value.getQuery());
                }
            }
            String finalQuery = getOriginalKeywords();
            for (String key : queryMap.keySet()) {
                finalQuery += " AND (" + queryMap.get(key) + ")";
            }
            try {
                searchResult = searchServer.search(finalQuery, null, null, sortMap);
            } catch (SearchException e) {
                error(e.getMessage());
                LOGGER.error("Can't do the search for " + searchKeywords, e);
            }
            facetsList = searchResult.getFacetsList();
            rosList = searchResult.getROsList();
            searchResultsList = new SimpleSearchResultsView("searchResultsListView", rosList, RESULTS_PER_PAGE);
            add(buildFiltersSortLinks());
        }
        searchResultsDiv.add((Component) searchResultsList);
        searchResultsDiv.setOutputMarkupId(true);

        FacetsView facetsView = new FacetsView("filters", getSelected(), new PropertyModel<List<FacetEntry>>(this,
                "facets"));
        facetsView.getListeners().add(this);
        add(facetsView);

        final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
        searchResultsDiv.add(noResults);
        //        noResults.setVisible(searchResults == null || searchResults.isEmpty());
        noResults.setVisible(false);

        add(new BootstrapPagingNavigator("pagination", searchResultsList));
        AjaxLink<Object> submitFilters = new AjaxLink<Object>("submitFilters") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Map<String, String> queryMap = new HashMap<>();
                target.add(SearchResultsPage.this);
                for (FacetValue value : getSelected()) {
                    if (queryMap.containsKey(value.getParamName())) {
                        String queryPart = queryMap.get(value.getParamName()) + " OR " + value.getQuery();
                        queryMap.put(value.getParamName(), queryPart);
                    } else {
                        queryMap.put(value.getParamName(), value.getQuery());
                    }
                }
                String finalQuery = getOriginalKeywords();
                for (String key : queryMap.keySet()) {
                    finalQuery += " AND (" + queryMap.get(key) + ")";
                }
                setResponsePage(new SearchResultsPage(finalQuery, getSelected(), getOriginalKeywords(), sortMap));
            }
        };
        add(submitFilters);
        clearFilters = new AjaxLink<Object>("clearFilters") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(originalKeywords, null, originalKeywords, sortMap));
            }
        };
        add(clearFilters);
    }


    private AbstractRepeater buildFiltersSortLinks() {
        RepeatingView sortView = new RepeatingView("sortListView");
        if (facetsList != null)
            for (final FacetEntry facet : facetsList) {
                if (facet.isSorteable()) {
                    sortView.add(createSortLink(sortView.newChildId(), facet, SortOrder.ASC));
                    sortView.add(createSortLink(sortView.newChildId(), facet, SortOrder.DESC));
                }
            }

        return sortView;
    }


    private WebMarkupContainer createSortLink(String id, final FacetEntry facet, final SearchServer.SortOrder order) {
        WebMarkupContainer item = new WebMarkupContainer(id);
        @SuppressWarnings("serial")
        AjaxLink<?> link = new AjaxLink<Void>("link") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                // this is to limit the sort to one field only, remove to allow more fields
                sortMap.clear();

                sortMap.put(facet.getFieldName(), order);
                setResponsePage(new SearchResultsPage(searchKeywords, savedSelected, originalKeywords, sortMap));
            }
        };
        if (sortMap.get(facet.getFieldName()) == order) {
            link.add(AttributeModifier.replace("class", "selected_filter_label"));
        }
        item.add(link);

        Label nameLabel = new Label("name", facet.getName() + (order == SortOrder.ASC ? " (asc)" : " (desc)"));
        link.add(nameLabel);

        return item;
    }


    public List<FacetValue> getSelected() {
        return selected;
    }


    public String getOriginalKeywords() {
        return this.originalKeywords;
    }


    public List<FacetEntry> getFacets() {
        return facetsList;
    }


    public static void populateItem(ListItem<FoundRO> item) {
        final FoundRO result = item.getModelObject();
        BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
        link.getPageParameters().add("ro", result.getResearchObject().getUri().toString());
        link.add(new Label("researchObject.name", new PropertyModel<String>(result, "researchObject.name")));
        item.add(link);
        item.add(new Label("researchObject.title", new PropertyModel<String>(result, "researchObject.title")));
        //            item.add(new CreatorsPanel("researchObject.creator", new PropertyModel<List<Creator>>(result,
        //                    "researchObject.creators")));
        item.add(new Label("researchObject.createdFormatted", new PropertyModel<String>(result,
                "researchObject.createdFormatted")));
        WebMarkupContainer score = new WebMarkupContainer("searchScore");
        item.add(score);
        score.setVisible(result.getScore() >= 0);
        score.add(new Label("scoreInPercent", new PropertyModel<String>(result, "scoreInPercent")));
        Label bar = new Label("percentBar", "");
        bar.add(new Behavior() {

            /** id. */
            private static final long serialVersionUID = -5409800651205755103L;


            @Override
            public void onComponentTag(final Component component, final ComponentTag tag) {
                super.onComponentTag(component, tag);
                tag.put("style", "width: " + Math.min(100, result.getScoreInPercent()) + "%");
            }
        });
        score.add(bar);
    }


    @Override
    public void onAjaxLinkClicked(Object source, AjaxRequestTarget target) {
        for (FacetValue val : selected) {
            if (val.getLabel().equals(((FacetValue) (source)).getLabel())
                    && val.getParamName().equals(((FacetValue) (source)).getParamName())) {
                selected.remove(val);
                searchKeywords = originalKeywords;
                for (FacetValue value : selected) {
                    searchKeywords += " AND " + value.getQuery();
                }
                return;
            }
        }

        selected.add((FacetValue) (source));
        searchKeywords = originalKeywords;
        for (FacetValue value : selected) {
            searchKeywords += " AND " + value.getQuery();
        }
    }


    @Override
    public void onSearchResultsAvailable(SearchResult searchResult) {
        if (facetsList == null) {
            facetsList = searchResult.getFacetsList();
            add(buildFiltersSortLinks());
        }
    }
}
