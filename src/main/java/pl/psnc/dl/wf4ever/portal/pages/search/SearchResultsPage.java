package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
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
public class SearchResultsPage extends Base implements IAjaxLinkListener {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SearchResultsPage.class);

    public static final int RESULTS_PER_PAGE = 15;

    private List<FacetEntry> facetsList = null;
    private List<FoundRO> ROsList = null;
    private String keywords = null;
    private List<FacetValue> selected = null;
    private List<FacetValue> savedSelected = null;
    private String originalKeywords = null;
    IPageable searchResultsList = null;
    ROSortMode roSortMode;


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
            final String originalKeywords, ROSortMode sortMode) {
        super(new PageParameters());
        savedSelected = new ArrayList<>();
        if (selected != null) {
            savedSelected.addAll(selected);
        }

        if (selected == null) {
            this.selected = new ArrayList<>();
        } else {
            this.selected = selected;
        }
        if (originalKeywords == null) {
            this.originalKeywords = searchKeywords;
        } else {
            this.originalKeywords = originalKeywords;
        }
        if (sortMode == null) {
            sortMode = ROSortMode.NAME;
        }
        roSortMode = sortMode;
        keywords = searchKeywords;
        setDefaultModel(new CompoundPropertyModel<SearchResultsPage>(this));
        SearchServer searchServer = ((PortalApplication) getApplication()).getSearchServer();

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
        searchResultsDiv.setOutputMarkupId(true);
        add(searchResultsDiv);

        AjaxLink<Object> clearFilters = new AjaxLink<Object>("clearFilters") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(getOriginalKeywords(), null, getOriginalKeywords(), roSortMode));
            }
        };
        add(clearFilters);
        AjaxLink<Object> sortByName = new AjaxLink<Object>("sortByName") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(keywords, savedSelected, getOriginalKeywords(), ROSortMode.NAME));
            }
        };
        add(sortByName);

        AjaxLink<Object> sortByNameDesc = new AjaxLink<Object>("sortByNameDesc") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(keywords, savedSelected, getOriginalKeywords(),
                        ROSortMode.NAME_DESC));
            }
        };
        add(sortByNameDesc);

        AjaxLink<Object> sortBySize = new AjaxLink<Object>("sortBySize") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(keywords, savedSelected, getOriginalKeywords(),
                        ROSortMode.NUMBER_OF_RESOURCES));
            }
        };
        add(sortBySize);

        AjaxLink<Object> sortBySizeDesc = new AjaxLink<Object>("sortBySizeDesc") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(keywords, savedSelected, originalKeywords,
                        ROSortMode.NUMBER_OF_RESOURCES_DESC));
            }
        };
        add(sortBySizeDesc);

        AjaxLink<Object> sortByDate = new AjaxLink<Object>("sortByDate") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(keywords, savedSelected, originalKeywords,
                        ROSortMode.CREATION_DATE));
            }
        };
        add(sortByDate);

        AjaxLink<Object> sortByDateDesc = new AjaxLink<Object>("sortByDateDesc") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(new SearchResultsPage(keywords, savedSelected, originalKeywords,
                        ROSortMode.CREATION_DATE_DESC));
            }
        };
        add(sortByDateDesc);

        searchResultsDiv.add(new Label("searchKeywords", this.originalKeywords));

        /*
        if (searchServer.supportsPagination()) {
            searchResultsList = new LazySearchResultsView("searchResultsListView", searchServer, searchKeywords,
                    RESULTS_PER_PAGE);
        } else {
            */
        SearchResult searchResult = null;
        Map<String, ORDER> sortMap = new HashedMap();
        switch (roSortMode) {
            case NAME:
                sortMap.put("uri", ORDER.asc);
                sortByName.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                break;
            case NAME_DESC:
                sortMap.put("uri", ORDER.desc);
                sortByNameDesc.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                break;
            case NUMBER_OF_RESOURCES:
                sortMap.put("resources_size", ORDER.asc);
                sortBySize.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                break;
            case NUMBER_OF_RESOURCES_DESC:
                sortMap.put("resources_size", ORDER.desc);
                sortBySizeDesc.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                break;
            case CREATION_DATE:
                sortMap.put("created", ORDER.asc);
                sortByDate.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                break;
            case CREATION_DATE_DESC:
                sortMap.put("created", ORDER.desc);
                sortByDateDesc.add(new SimpleAttributeModifier("class", "selected_filter_label"));
                break;
        }
        try {
            Map<String, String> queryMap = new HashedMap();
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
            searchResult = searchServer.search(finalQuery, null, null, sortMap);
        } catch (SearchException e) {
            error(e.getMessage());
            LOGGER.error("Can't do the search for " + searchKeywords, e);
        }
        facetsList = searchResult.getFactesList();
        ROsList = searchResult.getROsList();
        searchResultsList = new SimpleSearchResultsView("searchResultsListView", ROsList, RESULTS_PER_PAGE);
        //}
        searchResultsDiv.add((Component) searchResultsList);
        searchResultsDiv.setOutputMarkupId(true);
        //TODO to something as below
        //        FacetsView facetsView = new FacetsView("filters", new PropertyModel<List<Facet>>(searchResults, "facets"));
        FacetsView facetsView = new FacetsView("filters", getSelected(), new PropertyModel<List<FacetEntry>>(this,
                "facets"));
        facetsView.getListeners().add(this);
        add(facetsView);

        final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
        searchResultsDiv.add(noResults);
        //        noResults.setVisible(searchResults == null || searchResults.isEmpty());
        noResults.setVisible(false);

        add(new BootstrapPagingNavigator("pagination", searchResultsList));
        final Component parent = this;
        AjaxLink<Object> submitFilters = new AjaxLink<Object>("submitFilters") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Map<String, String> queryMap = new HashedMap();
                target.add(parent);
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
                setResponsePage(new SearchResultsPage(finalQuery, getSelected(), getOriginalKeywords(), roSortMode));
            }
        };
        add(submitFilters);

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
                keywords = originalKeywords;
                for (FacetValue value : selected) {
                    keywords += " AND " + value.getQuery();
                }
                return;
            }
        }

        selected.add((FacetValue) (source));
        keywords = originalKeywords;
        for (FacetValue value : selected) {
            keywords += " AND " + value.getQuery();
        }
    }
}
