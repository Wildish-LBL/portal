package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.ROHeader;
import pl.psnc.dl.wf4ever.portal.services.QueryFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class HomePage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;


	public HomePage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);

		QueryExecution x = QueryExecutionFactory.sparqlService(((PortalApplication) getApplication())
				.getSparqlEndpointURL().toString(), QueryFactory.getxMostRecentROs(10));
		ResultSet results = x.execSelect();
		List<ROHeader> roHeaders = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			URI uri = new URI(solution.getResource("resource").getURI());
			String author = solution.getLiteral("creator").getString();
			Calendar created = ((XSDDateTime)solution.getLiteral("created").getValue()).asCalendar();
			roHeaders.add(new ROHeader(uri, author, created));
		}

		ListView<ROHeader> list = new PropertyListView<ROHeader>("10recentROsListView", roHeaders) {

			private static final long serialVersionUID = -1782790193913483327L;


			@Override
			protected void populateItem(ListItem<ROHeader> item)
			{
				ROHeader ro = item.getModelObject();
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
				link.getPageParameters().add("ro", UrlEncoder.QUERY_INSTANCE.encode(ro.getUri().toString(), "UTF-8"));
				link.add(new Label("name"));
				item.add(link);
				item.add(new Label("author"));
				item.add(new Label("createdFormatted"));
			}
		};
		list.setReuseItems(true);
		add(list);

		add(new BookmarkablePageLink<Void>("faq", HelpPage.class));
		add(new BookmarkablePageLink<Void>("contact", ContactPage.class));

//		add(new Label("roCnt", "" + uris.size()));
		// FIXME does the below really work?
		add(new Label("usersOnlineCnt", "" + (getRequestLogger().getLiveSessions().length + 1)));

	}


	IRequestLogger getRequestLogger()
	{
		WebApplication webApplication = (WebApplication) Application.get();
		IRequestLogger requestLogger = webApplication.getRequestLogger();

		if (requestLogger == null)
			requestLogger = new RequestLogger();
		return requestLogger;
	}
}
