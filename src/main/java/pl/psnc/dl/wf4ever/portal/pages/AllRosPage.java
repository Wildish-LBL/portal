package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@AuthorizeInstantiation("USER")
public class AllRosPage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AllRosPage.class);


	public AllRosPage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);

		QueryExecution x = QueryExecutionFactory.sparqlService(((PortalApplication) getApplication())
				.getSparqlEndpointURL().toString(), MyQueryFactory.getxMostRecentROs(100));
		ResultSet results = x.execSelect();
		List<ResearchObject> roHeaders = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			URI uri = new URI(solution.getResource("resource").getURI());
			Creator author = RoFactory.getCreator(MySession.get().getUsernames(), solution.get("creator"));
			Calendar created = ((XSDDateTime) solution.getLiteral("created").getValue()).asCalendar();
			roHeaders.add(new ResearchObject(uri, created, Arrays.asList(author)));
		}

		add(new MyFeedbackPanel("feedbackPanel"));
		ListView<ResearchObject> list = new PropertyListView<ResearchObject>("rosListView", roHeaders) {

			private static final long serialVersionUID = -6310254217773728128L;


			@Override
			protected void populateItem(ListItem<ResearchObject> item)
			{
				ResearchObject ro = item.getModelObject();
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
				link.getPageParameters().add("ro", UrlEncoder.QUERY_INSTANCE.encode(ro.getURI().toString(), "UTF-8"));
				link.add(new Label("name"));
				item.add(link);
				item.add(new CreatorsPanel("creator", new PropertyModel<List<Creator>>(ro, "creators")));
				item.add(new Label("createdFormatted"));
			}

		};
		add(list);

		add(new MyFeedbackPanel("addFeedbackPanel"));
	}

}
