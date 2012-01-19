package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.SearchResult;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;
import pl.psnc.dl.wf4ever.portal.services.SearchService;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.sun.syndication.io.FeedException;

public class HomePage
	extends TemplatePage
{

	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(HomePage.class);

	private int roCnt;

	private int resourceCnt;

	private int annCnt;

	private List<SearchResult> searchResults;

	private String searchKeywords;


	@SuppressWarnings("serial")
	public HomePage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);
		setDefaultModel(new CompoundPropertyModel<HomePage>(this));

		QueryExecution x = QueryExecutionFactory.sparqlService(((PortalApplication) getApplication())
				.getSparqlEndpointURL().toString(), MyQueryFactory.getxMostRecentROs(10));
		ResultSet results = x.execSelect();
		List<ResearchObject> roHeaders = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			URI uri = new URI(solution.getResource("resource").getURI());
			String author = solution.getLiteral("creator").getString();
			Calendar created = ((XSDDateTime) solution.getLiteral("created").getValue()).asCalendar();
			roHeaders.add(new ResearchObject(uri, created, author));
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

		ListView<ResearchObject> list = new PropertyListView<ResearchObject>("10recentROsListView", roHeaders) {

			@Override
			protected void populateItem(ListItem<ResearchObject> item)
			{
				ResearchObject ro = item.getModelObject();
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
				link.getPageParameters().add("ro", UrlEncoder.QUERY_INSTANCE.encode(ro.getURI().toString(), "UTF-8"));
				link.add(new Label("name"));
				item.add(link);
				item.add(new Label("creator"));
				item.add(new Label("createdAgoFormatted"));
			}
		};
		list.setReuseItems(true);
		add(list);

		final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
		searchResultsDiv.setOutputMarkupId(true);
		add(searchResultsDiv);

		ListView<SearchResult> searchResultsList = new PropertyListView<SearchResult>("searchResultsListView",
				new PropertyModel<List<SearchResult>>(this, "searchResults")) {

			@Override
			protected void populateItem(ListItem<SearchResult> item)
			{
				SearchResult result = item.getModelObject();
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
				link.getPageParameters().add("ro",
					UrlEncoder.QUERY_INSTANCE.encode(result.getResearchObject().getURI().toString(), "UTF-8"));
				link.add(new Label("researchObject.name"));
				item.add(link);
				item.add(new Label("researchObject.title"));
				item.add(new Label("scoreInPercent"));
				item.add(new Label("researchObject.creator"));
				item.add(new Label("researchObject.createdAgoFormatted"));
			}
		};
		searchResultsList.setReuseItems(true);
		searchResultsDiv.add(searchResultsList);

		add(new BookmarkablePageLink<Void>("faq", HelpPage.class));
		add(new BookmarkablePageLink<Void>("contact", ContactPage.class));

		//		add(new Label("roCnt", "" + uris.size()));
		// FIXME does the below really work?
		add(new Label("usersOnlineCnt", "" + (getRequestLogger().getLiveSessions().length + 1)));

		Form< ? > searchForm = new Form<Void>("searchForm");
		add(searchForm);

		searchForm.add(new MyAjaxButton("searchButton", searchForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				if (getSearchKeywords() != null && !getSearchKeywords().isEmpty()) {
					URL searchEndpoint = ((PortalApplication) getApplication()).getSearchEndpointURL();
					try {
						List<SearchResult> results = SearchService.findByKeywords(searchEndpoint.toURI(),
							getSearchKeywords());
						setSearchResults(results);
						target.add(searchResultsDiv);
						target.appendJavaScript("$('#searchResultsLink').click();");
					}
					catch (IllegalArgumentException | FeedException | IOException | URISyntaxException e) {
						logger.error(e);
					}
				}
			}

		});

		searchForm.add(new RequiredTextField<String>("keywords", new PropertyModel<String>(this, "searchKeywords")));

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


	/**
	 * @return the searchResults
	 */
	public List<SearchResult> getSearchResults()
	{
		return searchResults;
	}


	/**
	 * @param searchResults
	 *            the searchResults to set
	 */
	public void setSearchResults(List<SearchResult> searchResults)
	{
		this.searchResults = searchResults;
	}


	/**
	 * @return the searchKeywords
	 */
	public String getSearchKeywords()
	{
		return searchKeywords;
	}


	/**
	 * @param searchKeywords
	 *            the searchKeywords to set
	 */
	public void setSearchKeywords(String searchKeywords)
	{
		this.searchKeywords = searchKeywords;
	}
}
