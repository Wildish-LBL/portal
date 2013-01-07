package pl.psnc.dl.wf4ever.portal.pages.home;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.Creator;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.Recommendation;
import pl.psnc.dl.wf4ever.portal.model.SearchResult;
import pl.psnc.dl.wf4ever.portal.pages.ContactPage;
import pl.psnc.dl.wf4ever.portal.pages.HelpPage;
import pl.psnc.dl.wf4ever.portal.pages.TemplatePage;
import pl.psnc.dl.wf4ever.portal.pages.all.AllRosPage;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;
import pl.psnc.dl.wf4ever.portal.services.RODLUtilities;
import pl.psnc.dl.wf4ever.portal.services.RSSService;
import pl.psnc.dl.wf4ever.portal.services.RecommenderService;
import pl.psnc.dl.wf4ever.portal.services.SearchService;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.sun.syndication.io.FeedException;

/**
 * The home page.
 * 
 * @author piotrekhol
 * 
 */
public class HomePage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(HomePage.class);

    /** Number of ROs in RODL. */
    private int roCnt;

    /** Number of resources in RODL. */
    private int resourceCnt;

    /** Number of annotations in RODL. */
    private int annCnt;

    /** Search results. */
    private List<SearchResult> searchResults;

    /** Recommendations. */
    private List<Recommendation> recommendations;

    /** Search keywords input by users. */
    private String searchKeywords;

    /** User myExp id used for recommendations. */
    private String myExpId;


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     */
    @SuppressWarnings("serial")
    public HomePage(final PageParameters parameters)
            throws IOException {
        super(parameters);
        setDefaultModel(new CompoundPropertyModel<HomePage>(this));

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        ResultSet results;
        List<ResearchObject> roHeaders = RODLUtilities.getMostRecentROs(
            ((PortalApplication) getApplication()).getSparqlEndpointURI(), rodlURI, MySession.get().getUsernames(), 10);

        results = QueryExecutionFactory.sparqlService(
            ((PortalApplication) getApplication()).getSparqlEndpointURI().toString(),
            MyQueryFactory.getResourcesCount("ro:ResearchObject")).execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.next();
            setRoCnt(solution.getLiteral(solution.varNames().next()).getInt());
        }
        add(new Label("roCnt"));

        results = QueryExecutionFactory.sparqlService(
            ((PortalApplication) getApplication()).getSparqlEndpointURI().toString(),
            MyQueryFactory.getResourcesCount("ro:Resource")).execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.next();
            setResourceCnt(solution.getLiteral(solution.varNames().next()).getInt());
        }
        add(new Label("resourceCnt"));

        results = QueryExecutionFactory.sparqlService(
            ((PortalApplication) getApplication()).getSparqlEndpointURI().toString(),
            MyQueryFactory.getResourcesCount("ro:AggregatedAnnotation")).execSelect();
        if (results.hasNext()) {
            QuerySolution solution = results.next();
            setAnnCnt(solution.getLiteral(solution.varNames().next()).getInt());
        }
        add(new Label("annCnt"));

        add(new ExternalLink("recentROsRSSFeed", RSSService.RECENT_ROS_FILENAME));

        ListView<ResearchObject> list = new RecentROsListView("10recentROsListView", roHeaders);
        list.setReuseItems(true);
        add(list);

        add(new BookmarkablePageLink<>("allRos", AllRosPage.class));

        final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
        searchResultsDiv.setOutputMarkupId(true);
        add(searchResultsDiv);

        final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
        searchResultsDiv.add(noResults);

        ListView<SearchResult> searchResultsList = new SearchResultsListView("searchResultsListView",
                new PropertyModel<List<SearchResult>>(this, "searchResults"));
        searchResultsList.setReuseItems(true);
        searchResultsDiv.add(searchResultsList);

        add(new BookmarkablePageLink<Void>("faq", HelpPage.class));
        add(new BookmarkablePageLink<Void>("contact", ContactPage.class));

        // FIXME does the below really work?
        add(new Label("usersOnlineCnt", "" + (getRequestLogger().getLiveSessions().length + 1)));

        Form<?> searchForm = new Form<Void>("searchForm");
        add(searchForm);

        searchForm.add(new MyAjaxButton("searchButton", searchForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if (getSearchKeywords() != null && !getSearchKeywords().isEmpty()) {
                    URL searchEndpoint = ((PortalApplication) getApplication()).getSearchEndpointURL();
                    try {
                        List<SearchResult> results = SearchService.findByKeywords(rodlURI, searchEndpoint.toURI(),
                            getSearchKeywords());
                        setSearchResults(results);
                        getSession().cleanupFeedbackMessages();
                        noResults.setVisible(results == null || results.isEmpty());
                        target.add(searchResultsDiv);
                        target.appendJavaScript("$('#searchResultsLink').click();");
                    } catch (IllegalArgumentException | FeedException | IOException | URISyntaxException e) {
                        LOG.error(e);
                        error(e.getMessage());
                    }
                    target.add(feedbackPanel);
                }
            }
        });

        searchForm.add(new RequiredTextField<String>("keywords", new PropertyModel<String>(this, "searchKeywords")));

        final WebMarkupContainer recommendationsDiv = new WebMarkupContainer("recommendationsDiv");
        recommendationsDiv.setOutputMarkupId(true);
        add(recommendationsDiv);

        final WebMarkupContainer noRecommendations = new WebMarkupContainer("noRecommendations");
        recommendationsDiv.add(noRecommendations);

        ListView<Recommendation> recommendationsList = new PropertyListView<Recommendation>("recommendationsList",
                new PropertyModel<List<Recommendation>>(this, "recommendations")) {

            @Override
            protected void populateItem(ListItem<Recommendation> item) {
                final Recommendation rec = item.getModelObject();
                item.add(new ExternalLink("link", new PropertyModel<String>(rec, "resource.toString"),
                        new PropertyModel<String>(rec, "title")));
                item.add(new Label("strength"));
            }
        };
        recommendationsList.setReuseItems(true);
        recommendationsDiv.add(recommendationsList);

        final Form<?> recommenderForm = new Form<Void>("recommenderForm");
        recommenderForm.setOutputMarkupId(true);
        recommendationsDiv.add(recommenderForm);

        recommenderForm.add(new RequiredTextField<String>("myExpId", new PropertyModel<String>(this, "myExpId")));
        recommenderForm.add(new MyAjaxButton("confirmMyExpId", recommenderForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                if (myExpId != null) {
                    try {
                        URI baseURI = ((PortalApplication) getApplication()).getRecommenderEndpointURL().toURI();
                        List<Recommendation> recs = RecommenderService.getRecommendations(baseURI, myExpId, 10);
                        setRecommendations(recs);
                        getSession().cleanupFeedbackMessages();
                        recommenderForm.setVisible(recs == null || recs.isEmpty());
                        noRecommendations.setVisible(recs == null || recs.isEmpty());
                        target.add(recommendationsDiv);
                    } catch (Exception e) {
                        LOG.error(e);
                        error(e.getMessage());
                    }
                } else {
                    error("myExperiment ID cannot be empty");
                }
                target.add(feedbackPanel);
            }
        });

    }


    /**
     * Get a request logger, used to track active sessions.
     * 
     * TODO: does it work?
     * 
     * @return a request logger
     */
    IRequestLogger getRequestLogger() {
        WebApplication webApplication = (WebApplication) Application.get();
        IRequestLogger requestLogger = webApplication.getRequestLogger();

        if (requestLogger == null) {
            requestLogger = new RequestLogger();
        }
        return requestLogger;
    }


    public int getRoCnt() {
        return roCnt;
    }


    public void setRoCnt(int roCnt) {
        this.roCnt = roCnt;
    }


    public int getResourceCnt() {
        return resourceCnt;
    }


    public void setResourceCnt(int resourceCnt) {
        this.resourceCnt = resourceCnt;
    }


    public int getAnnCnt() {
        return annCnt;
    }


    public void setAnnCnt(int annCnt) {
        this.annCnt = annCnt;
    }


    public List<SearchResult> getSearchResults() {
        return searchResults;
    }


    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }


    public String getSearchKeywords() {
        return searchKeywords;
    }


    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }


    public String getMyExpId() {
        return myExpId;
    }


    public void setMyExpId(String myExpId) {
        this.myExpId = myExpId;
    }


    public List<Recommendation> getRecommendations() {
        return recommendations;
    }


    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }


    /**
     * A {@link PropertyListView} of search results.
     * 
     * @author piotrekhol
     * 
     */
    private final class SearchResultsListView extends PropertyListView<SearchResult> {

        /** id. */
        private static final long serialVersionUID = 915182420617753899L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param model
         *            model of search results
         */
        private SearchResultsListView(String id, IModel<? extends List<? extends SearchResult>> model) {
            super(id, model);
        }


        @Override
        protected void populateItem(ListItem<SearchResult> item) {
            final SearchResult result = item.getModelObject();
            BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
            link.getPageParameters().add("ro", result.getResearchObject().getUri().toString());
            link.add(new Label("researchObject.name"));
            item.add(link);
            item.add(new Label("researchObject.title"));
            item.add(new Label("scoreInPercent"));
            item.add(new CreatorsPanel("researchObject.creator", new PropertyModel<List<Creator>>(result,
                    "researchObject.creators")));
            item.add(new Label("researchObject.createdFormatted"));
            Label bar = new Label("percentBar", "");
            bar.add(new Behavior() {

                /**
                 * 
                 */
                private static final long serialVersionUID = -5409800651205755103L;


                @Override
                public void onComponentTag(final Component component, final ComponentTag tag) {
                    super.onComponentTag(component, tag);
                    tag.put("style", "width: " + Math.min(100, result.getScoreInPercent()) + "%");
                }
            });
            item.add(bar);
        }
    }


    /**
     * A {@link PropertyListView} that displays most recent ROs. ROs that have unparseable creation dates are not
     * included.
     * 
     * @author piotrekhol
     * 
     */
    private final class RecentROsListView extends PropertyListView<ResearchObject> {

        /** id. */
        private static final long serialVersionUID = -6714165053677665740L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param list
         *            list of research objects to display
         */
        private RecentROsListView(String id, List<? extends ResearchObject> list) {
            super(id, list);
        }


        @Override
        protected void populateItem(ListItem<ResearchObject> item) {
            ResearchObject ro = item.getModelObject();
            BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
            link.getPageParameters().add("ro", ro.getUri().toString());
            link.add(new Label("name"));
            item.add(link);
            item.add(new CreatorsPanel("creator", new PropertyModel<List<Creator>>(ro, "creators")));
            item.add(new Label("createdFormatted"));
        }
    }
}
