package pl.psnc.dl.wf4ever.portal.pages.all;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.format.ISODateTimeFormat;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;
import pl.psnc.dl.wf4ever.portal.services.RoFactory;

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
public class AllRosPage extends Base {

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
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            URI uri = new URI(solution.getResource("ro").getURI());
            int resCnt = solution.getLiteral("resCnt").getInt();
            Literal creators = solution.getLiteral("creators");
            List<Creator> authors = new ArrayList<>();
            if (creators != null) {
                for (String creator : creators.getString().split(", ")) {
                    authors.add(RoFactory.getCreator(rodlURI, MySession.get().getUsernames(), creator));
                }
            }
            Calendar created = null;
            Object date = solution.getLiteral("created").getValue();
            if (date instanceof XSDDateTime) {
                created = ((XSDDateTime) date).asCalendar();
            } else {
                try {
                    created = ISODateTimeFormat.dateTime().parseDateTime(date.toString()).toGregorianCalendar();
                } catch (IllegalArgumentException e) {
                    LOG.warn("Don't know how to parse date: " + date);
                }
            }
            ResearchObject ro = new ResearchObject(uri, created, authors);
            roHeaders.add(ro);
            resCnts.put(ro, resCnt);
        }

        ListView<ResearchObject> list = new PropertyListView<ResearchObject>("rosListView", roHeaders) {

            private static final long serialVersionUID = -6310254217773728128L;


            @Override
            protected void populateItem(ListItem<ResearchObject> item) {
                ResearchObject ro = item.getModelObject();
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
                link.getPageParameters().add("ro", ro.getURI().toString());
                link.add(new Label("name"));
                item.add(link);
                item.add(new Label("resourcesCnt", "" + resCnts.get(ro)));
                item.add(new CreatorsPanel("creators", new PropertyModel<List<Creator>>(ro, "creators")));
                item.add(new Label("createdFormatted"));
            }

        };
        add(list);
    }
}
