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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.ROHeader;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class HomePage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private int roCnt;

	private int resourceCnt;

	private int annCnt;


	public HomePage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);
		setDefaultModel(new CompoundPropertyModel<HomePage>(this));

		QueryExecution x = QueryExecutionFactory.sparqlService(((PortalApplication) getApplication())
				.getSparqlEndpointURL().toString(), MyQueryFactory.getxMostRecentROs(10));
		ResultSet results = x.execSelect();
		List<ROHeader> roHeaders = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			URI uri = new URI(solution.getResource("resource").getURI());
			String author = solution.getLiteral("creator").getString();
			Calendar created = ((XSDDateTime) solution.getLiteral("created").getValue()).asCalendar();
			roHeaders.add(new ROHeader(uri, author, created));
		}

		results = QueryExecutionFactory.sparqlService(
			((PortalApplication) getApplication()).getSparqlEndpointURL().toString(),
			MyQueryFactory.getResourcesCount("ro:ResearchObject")).execSelect();
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			setRoCnt(solution.getLiteral(solution.varNames().next()).getInt());
		}
		add(new Label("roCnt"));

		results = QueryExecutionFactory.sparqlService(
			((PortalApplication) getApplication()).getSparqlEndpointURL().toString(),
			MyQueryFactory.getResourcesCount("ro:Resource")).execSelect();
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			setResourceCnt(solution.getLiteral(solution.varNames().next()).getInt());
		}
		add(new Label("resourceCnt"));

		results = QueryExecutionFactory.sparqlService(
			((PortalApplication) getApplication()).getSparqlEndpointURL().toString(),
			MyQueryFactory.getResourcesCount("ro:AggregatedAnnotation")).execSelect();
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			setAnnCnt(solution.getLiteral(solution.varNames().next()).getInt());
		}
		add(new Label("annCnt"));

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


	/**
	 * @return the roCnt
	 */
	public int getRoCnt()
	{
		return roCnt;
	}


	/**
	 * @param roCnt
	 *            the roCnt to set
	 */
	public void setRoCnt(int roCnt)
	{
		this.roCnt = roCnt;
	}


	/**
	 * @return the resourceCnt
	 */
	public int getResourceCnt()
	{
		return resourceCnt;
	}


	/**
	 * @param resourceCnt
	 *            the resourceCnt to set
	 */
	public void setResourceCnt(int resourceCnt)
	{
		this.resourceCnt = resourceCnt;
	}


	/**
	 * @return the annCnt
	 */
	public int getAnnCnt()
	{
		return annCnt;
	}


	/**
	 * @param annCnt
	 *            the annCnt to set
	 */
	public void setAnnCnt(int annCnt)
	{
		this.annCnt = annCnt;
	}
}
