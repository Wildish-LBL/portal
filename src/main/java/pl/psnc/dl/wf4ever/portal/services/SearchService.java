/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;

import pl.psnc.dl.wf4ever.portal.model.ResearchObject;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @author piotrek
 * 
 */
public class SearchService
{

	private final static Logger logger = Logger.getLogger(SearchService.class);

	private static final String DL_QUERY_NS = "http://dlibra.psnc.pl/opensearch/";

	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");


	@SuppressWarnings("unchecked")
	public static LinkedHashMap<ResearchObject, Double> findByKeywords(URI baseURI, String keywords)
		throws IllegalArgumentException, MalformedURLException, FeedException, IOException
	{
		URI queryURI = new UriBuilderImpl().uri(baseURI).queryParam("searchTerms", keywords)
				.queryParam("aggregate", "false").queryParam("count", 50).build();

		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(queryURI.toURL()));

		List<SyndEntry> entries = feed.getEntries();
		LinkedHashMap<ResearchObject, Double> ros = new LinkedHashMap<>();
		for (SyndEntry entry : entries) {
			URI researchObjectURI = null;
			Calendar created = null;
			String creator = null, title = null;
			double score = -1;
			List<Element> dlMarkup = (List<Element>) entry.getForeignMarkup();
			for (Element element : dlMarkup) {
				if (!DL_QUERY_NS.equals(element.getNamespaceURI()))
					continue;
				switch (element.getName()) {
					case "attribute":
						switch (element.getAttributeValue("name")) {
							case "Identifier":
								researchObjectURI = URI.create(element.getValue());
								break;
							case "Creator":
								creator = element.getValue();
								break;
							case "Created":
								try {
									created = Calendar.getInstance();
									created.setTime(SDF.parse(element.getValue()));
								}
								catch (ParseException e) {
									logger.warn("Incorrect date", e);
									created = null;
								}
								break;
							case "Title":
								title = element.getValue();
								break;
						}
						break;
					case "score":
						score = Double.parseDouble(element.getValue());
						break;
				}
			}

			if (researchObjectURI != null && score != -1) {
				ResearchObject ro = new ResearchObject(researchObjectURI, created, creator);
				ro.setTitle(title);
				ros.put(ro, score);
			}
		}

		return ros;
	}
}
