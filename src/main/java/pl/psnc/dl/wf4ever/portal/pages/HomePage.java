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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.IRequestLogger;
import org.apache.wicket.protocol.http.RequestLogger;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.format.ISODateTimeFormat;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.model.Recommendation;
import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.model.SearchResult;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;
import pl.psnc.dl.wf4ever.portal.services.RecommenderService;
import pl.psnc.dl.wf4ever.portal.services.SearchService;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
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

	private List<Recommendation> recommendations;

	private String searchKeywords;

	private String myExpId;


	@SuppressWarnings("serial")
	public HomePage(final PageParameters parameters)
		throws Exception
	{
		super(parameters);
		setDefaultModel(new CompoundPropertyModel<HomePage>(this));

		final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);

		QueryExecution x = QueryExecutionFactory.sparqlService(((PortalApplication) getApplication())
				.getSparqlEndpointURL().toString(), MyQueryFactory.getxMostRecentROs(10));
		ResultSet results = x.execSelect();
		List<ResearchObject> roHeaders = new ArrayList<>();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			URI uri = new URI(solution.getResource("ro").getURI());
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
			}
			else {
				try {
					created = ISODateTimeFormat.dateTime().parseDateTime(date.toString()).toGregorianCalendar();
				}
				catch (IllegalArgumentException e) {
					logger.warn("Don't know how to parse date: " + date);
				}
			}
			roHeaders.add(new ResearchObject(uri, created, authors));
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
				item.add(new CreatorsPanel("creator", new PropertyModel<List<Creator>>(ro, "creators")));
				item.add(new Label("createdFormatted"));
			}
		};
		list.setReuseItems(true);
		add(list);

		add(new BookmarkablePageLink<>("allRos", AllRosPage.class));

		final WebMarkupContainer searchResultsDiv = new WebMarkupContainer("searchResultsDiv");
		searchResultsDiv.setOutputMarkupId(true);
		add(searchResultsDiv);

		final WebMarkupContainer noResults = new WebMarkupContainer("noResults");
		searchResultsDiv.add(noResults);

		ListView<SearchResult> searchResultsList = new PropertyListView<SearchResult>("searchResultsListView",
				new PropertyModel<List<SearchResult>>(this, "searchResults")) {

			@Override
			protected void populateItem(ListItem<SearchResult> item)
			{
				final SearchResult result = item.getModelObject();
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
				link.getPageParameters().add("ro",
					UrlEncoder.QUERY_INSTANCE.encode(result.getResearchObject().getURI().toString(), "UTF-8"));
				link.add(new Label("researchObject.name"));
				item.add(link);
				item.add(new Label("researchObject.title"));
				item.add(new Label("scoreInPercent"));
				item.add(new CreatorsPanel("researchObject.creator", new PropertyModel<List<Creator>>(result,
						"researchObject.creators")));
				item.add(new Label("researchObject.createdFormatted"));
				Label bar = new Label("percentBar", "");
				bar.add(new Behavior() {

					@Override
					public void onComponentTag(final Component component, final ComponentTag tag)
					{
						super.onComponentTag(component, tag);
						tag.put("style", "width: " + Math.min(100, result.getScoreInPercent()) + "%");
					}
				});
				item.add(bar);
			}
		};
		searchResultsList.setReuseItems(true);
		searchResultsDiv.add(searchResultsList);

		add(new BookmarkablePageLink<Void>("faq", HelpPage.class));
		add(new BookmarkablePageLink<Void>("contact", ContactPage.class));

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
						List<SearchResult> results = SearchService.findByKeywords(rodlURI, searchEndpoint.toURI(),
							getSearchKeywords());
						setSearchResults(results);
						getSession().cleanupFeedbackMessages();
						noResults.setVisible(results == null || results.isEmpty());
						target.add(searchResultsDiv);
						target.appendJavaScript("$('#searchResultsLink').click();");
					}
					catch (IllegalArgumentException | FeedException | IOException | URISyntaxException e) {
						logger.error(e);
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
			protected void populateItem(ListItem<Recommendation> item)
			{
				final Recommendation rec = item.getModelObject();
				item.add(new ExternalLink("link", new PropertyModel<String>(rec, "resource.toString"),
						new PropertyModel<String>(rec, "title")));
				item.add(new Label("strength"));
			}
		};
		recommendationsList.setReuseItems(true);
		recommendationsDiv.add(recommendationsList);

		final Form< ? > recommenderForm = new Form<Void>("recommenderForm");
		recommenderForm.setOutputMarkupId(true);
		recommendationsDiv.add(recommenderForm);

		recommenderForm.add(new RequiredTextField<String>("myExpId", new PropertyModel<String>(this, "myExpId")));
		recommenderForm.add(new MyAjaxButton("confirmMyExpId", recommenderForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
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
					}
					catch (Exception e) {
						logger.error(e);
						error(e.getMessage());
					}
				}
				else {
					error("myExperiment ID cannot be empty");
				}
				target.add(feedbackPanel);
			}
		});

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


	/**
	 * @return the myExpId
	 */
	public String getMyExpId()
	{
		return myExpId;
	}


	/**
	 * @param myExpId
	 *            the myExpId to set
	 */
	public void setMyExpId(String myExpId)
	{
		this.myExpId = myExpId;
	}


	/**
	 * @return the recommendations
	 */
	public List<Recommendation> getRecommendations()
	{
		return recommendations;
	}


	/**
	 * @param recommendations
	 *            the recommendations to set
	 */
	public void setRecommendations(List<Recommendation> recommendations)
	{
		this.recommendations = recommendations;
	}
}
