package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.purl.wf4ever.rosrs.client.Creator;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.users.UserManagementService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;

/**
 * A page with a list of all ROs in RODL.
 * 
 * @author piotrekhol
 * 
 */
public class AllRosPage extends BasePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AllRosPage.class);


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     * @throws URISyntaxException
     *             RODL returned an invalid RO URI
     */
    public AllRosPage(final PageParameters parameters)
            throws IOException, URISyntaxException {
        super(parameters);

        add(new MyFeedbackPanel("feedbackPanel"));

        QueryExecution x = QueryExecutionFactory.sparqlService(((PortalApplication) getApplication())
                .getSparqlEndpointURI().toString(), MyQueryFactory.getAllROs());
        ResultSet results = x.execSelect();
        List<ResearchObject> roHeaders = new ArrayList<>();
        final Map<ResearchObject, Integer> resCnts = new HashMap<>();
        Map<URI, Creator> usernames = MySession.get().getUsernames();
        UserManagementService ums = MySession.get().getUms();
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            URI uri = new URI(solution.getResource("ro").getURI());
            int resCnt = solution.getLiteral("resCnt").getInt();
            Literal creators = solution.getLiteral("creators");
            Set<Creator> authors = new HashSet<>();
            if (creators != null) {
                for (String creator : creators.getString().split(", ")) {
                    authors.add(Creator.get(ums, usernames, creator));
                }
            }
            DateTime created = null;
            Object date = solution.getLiteral("created").getValue();
            if (date instanceof XSDDateTime) {
                created = new DateTime(((XSDDateTime) date).asCalendar().getTimeInMillis());
            } else {
                try {
                    created = new DateTime(ISODateTimeFormat.dateTime().parseDateTime(date.toString())
                            .toGregorianCalendar().getTimeInMillis());
                } catch (IllegalArgumentException e) {
                    LOG.warn("Don't know how to parse date: " + date);
                }
            }
            ResearchObject ro = new ResearchObject(uri, null);
            ro.setCreated(created);
            ro.setCreators(authors);
            roHeaders.add(ro);
            resCnts.put(ro, resCnt);
        }

        ListView<ResearchObject> list = new PropertyListView<ResearchObject>("rosListView", roHeaders) {

            private static final long serialVersionUID = -6310254217773728128L;


            @Override
            protected void populateItem(ListItem<ResearchObject> item) {
                ResearchObject ro = item.getModelObject();
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
                link.getPageParameters().add("ro", ro.getUri().toString());
                link.add(new Label("name"));
                item.add(link);
                item.add(new Label("resourcesCnt", "" + resCnts.get(ro)));
                item.add(new CreatorsPanel("creators", new PropertyModel<Set<Creator>>(ro, "creators")));
                item.add(new Label("createdFormatted"));
            }

        };
        add(list);
    }
}
