/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import pl.psnc.dl.wf4ever.portal.model.ResearchObject;

import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.module.opensearch.entity.OSQuery;
import com.sun.syndication.feed.module.opensearch.impl.OpenSearchModuleImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * @author piotrek
 * 
 */
public class SearchService
{

	@SuppressWarnings("unchecked")
	public static List<ResearchObject> findByKeywords(List<String> keywords)
	{
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("atom_1.0");

		// Add the opensearch module, you would get information like totalResults from the
		// return results of your search
		List<Module> mods = feed.getModules();
		OpenSearchModule osm = new OpenSearchModuleImpl();
		osm.setItemsPerPage(1);
		osm.setStartIndex(1);
		osm.setTotalResults(20);
		osm.setItemsPerPage(10);

		OSQuery query = new OSQuery();
		query.setRole("superset");
		query.setSearchTerms(StringUtils.join(keywords.toArray(), "+"));
		query.setStartPage(1);
		osm.addQuery(query);

		Link link = new Link();
		link.setHref("http://sandbox.wf4ever-project.org/opensearch/descriptor_en.xml");
		link.setType("application/opensearchdescription+xml");
		osm.setLink(link);

		mods.add(osm);

		feed.setModules(mods);
		// end add module

		List<SyndEntry> entries = feed.getEntries();
		List<ResearchObject> ros = new ArrayList<>();
		for (SyndEntry entry : entries) {
			// ResearchObject ro = new ResearchObject(researchObjectURI, created,
			// creator);
			// ros.add(ro);
			System.out.println(entry);
		}

		return ros;
	}
}
