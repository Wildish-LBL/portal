package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.SearchServer.SortOrder;
import org.purl.wf4ever.rosrs.client.search.SolrSearchServer;
import org.purl.wf4ever.rosrs.client.search.dataclasses.SearchResult;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.search.SearchResultsPage;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * The home page.
 * 
 * @author piotrekhol
 * 
 */
public class HomePage extends BasePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(HomePage.class);


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     */
    public HomePage(final PageParameters parameters)
            throws IOException {
        super(parameters);
        setDefaultModel(new CompoundPropertyModel<HomePage>(this));
        PortalApplication app = (PortalApplication) getApplication();

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        add(new Label("roCnt", getResearchObjectCount()));
        add(new Label("resourceCnt", getCount("ro:Resource")));
        add(new Label("annCnt", getCount("ro:AggregatedAnnotation")));

        //        add(new ExternalLink("recentROsRSSFeed", RSSService.RECENT_ROS_FILENAME));

        List<ResearchObject> recentROs = app.getRecentROs(3);
        ListView<ResearchObject> recentROList = new ROsListView("recent-ros", recentROs);
        recentROList.setReuseItems(true);
        add(recentROList);

        ListView<ResearchObject> featuredROList = new ROsListView("featured-ros", app.getFeaturedROs());
        featuredROList.setReuseItems(true);
        add(featuredROList);

        add(new BookmarkablePageLink<>("browse", SearchResultsPage.class));
        WebMarkupContainer signInDiv = new WebMarkupContainer("sign-in-div");
        signInDiv.setVisible(!MySession.get().isSignedIn());
        add(signInDiv);
        signInDiv.add(new BookmarkablePageLink<>("sign-in", app.getSignInPageClass()));
    }


    /**
     * Find the number of ROs using Solr, or SPARQL if Solr is not available.
     * 
     * @return the number of ROs in RODL.
     */
    private long getResearchObjectCount() {
        SearchServer searchServer = ((PortalApplication) getApplication()).getSearchServer();
        if (searchServer instanceof SolrSearchServer) {
            try {
                SearchResult result = searchServer.search("*:*", 0, 1, Collections.<String, SortOrder> emptyMap());
                return result.getNumFound();
            } catch (SearchException e) {
                LOG.error("Can't search for recent ROs using SOLR", e);
            }
        }
        // Solr is not available or returned an error
        return getCount("ro:ResearchObject");

    }


    /**
     * Calculate number of objects with an RDF class using SPARQL.
     * 
     * @param rdfClass
     *            RDF class.
     * @return count or 0 if error
     */
    private int getCount(String rdfClass) {
        try {
            ResultSet results = QueryExecutionFactory.sparqlService(
                ((PortalApplication) getApplication()).getSparqlEndpointURI().toString(),
                MyQueryFactory.getResourcesCount(rdfClass)).execSelect();
            if (results.hasNext()) {
                QuerySolution solution = results.next();
                return solution.getLiteral(solution.varNames().next()).getInt();
            }
        } catch (Exception e) {
            LOG.error("Can't get the count of " + rdfClass, e);
        }
        return 0;
    }


    /**
     * A {@link PropertyListView} that displays ROs. ROs that have unparseable creation dates are not included.
     * 
     * @author piotrekhol
     * 
     */
    private final class ROsListView extends PropertyListView<ResearchObject> {

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
        private ROsListView(String id, List<? extends ResearchObject> list) {
            super(id, list);
        }


        @Override
        protected void populateItem(ListItem<ResearchObject> item) {
            ResearchObject ro = item.getModelObject();
            BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
            link.getPageParameters().add("ro", ro.getUri().toString());
            link.add(new Label("name"));
            item.add(link);
            item.add(new Label("creator", new PropertyModel<String>(ro, "author.name")));
            item.add(new Label("createdFormatted"));
            item.add(new WebMarkupContainer("separator").setVisible(item.getIndex() < this.getViewSize() - 1));
        }
    }
}
