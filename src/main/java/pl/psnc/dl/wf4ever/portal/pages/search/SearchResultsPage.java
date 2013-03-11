package pl.psnc.dl.wf4ever.portal.pages.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
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
import org.purl.wf4ever.rosrs.client.search.Facet;
import org.purl.wf4ever.rosrs.client.search.FacetValue;
import org.purl.wf4ever.rosrs.client.search.FoundRO;
import org.purl.wf4ever.rosrs.client.search.SearchServer;

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

    public static final int RESULTS_PER_PAGE = 10;


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     */
    public SearchResultsPage(final String searchKeywords) {
        super(new PageParameters());
        setDefaultModel(new CompoundPropertyModel<SearchResultsPage>(this));

        SearchServer searchServer = ((PortalApplication) getApplication()).getSearchServer();

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
        searchResultsDiv.setOutputMarkupId(true);
        add(searchResultsDiv);

        searchResultsDiv.add(new Label("searchKeywords", searchKeywords));

        IPageable searchResultsList = null;
        /*
        if (searchServer.supportsPagination()) {
            searchResultsList = new LazySearchResultsView("searchResultsListView", searchServer, searchKeywords,
                    RESULTS_PER_PAGE);
        } else {
            */
        List<FoundRO> searchResults = null;
        try {
            searchResults = searchServer.search(searchKeywords).getROsList();
        } catch (SearchException e) {
            error(e.getMessage());
            LOGGER.error("Can't do the search for " + searchKeywords, e);
        }
        searchResultsList = new SimpleSearchResultsView("searchResultsListView", searchResults, RESULTS_PER_PAGE);
        //}
        searchResultsDiv.add((Component) searchResultsList);

        //TODO to something as below
        //        FacetsView facetsView = new FacetsView("filters", new PropertyModel<List<Facet>>(searchResults, "facets"));
        FacetsView facetsView = new FacetsView("filters", new PropertyModel<List<Facet>>(this, "mockupFacets"));
        facetsView.getListeners().add(this);
        add(facetsView);

        final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
        searchResultsDiv.add(noResults);
        //        noResults.setVisible(searchResults == null || searchResults.isEmpty());
        noResults.setVisible(false);

        add(new BootstrapPagingNavigator("pagination", searchResultsList));
    }


    public List<Facet> getMockupFacets() {
        Facet facet1 = new Facet(null, "Some facet");
        facet1.setName("facet1");
        facet1.getValues().add(new FacetValue("option1a", 2));
        facet1.getValues().add(new FacetValue("option1b", 5));
        facet1.getValues().add(new FacetValue("option1c", 15));
        Facet facet2 = new Facet(null, "Some facet 2");
        facet2.setName("facet2");
        facet2.getValues().add(new FacetValue("option2a", 23));
        facet2.getValues().add(new FacetValue("option2b", 35));
        facet2.getValues().add(new FacetValue("option2c", 15));
        Facet facet3 = new Facet(null, "Some facet 3");
        facet3.setName("facet3");
        facet3.getValues().add(new FacetValue("option3a", 2));
        facet3.getValues().add(new FacetValue("option3b", 334245));
        facet3.getValues().add(new FacetValue("option3c", 15));

        List<Facet> facets = new ArrayList<>();
        facets.add(facet1);
        facets.add(facet2);
        facets.add(facet3);
        return facets;
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
        FacetValue facetValue = (FacetValue) source;
        //TODO pass this somehow to the search server or results list view or reload the page...
        target.appendJavaScript("window.alert('Received a click from facet value: " + facetValue.getLabel() + "');");
    }
}
