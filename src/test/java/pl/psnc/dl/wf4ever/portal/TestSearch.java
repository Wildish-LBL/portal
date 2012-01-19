package pl.psnc.dl.wf4ever.portal;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import pl.psnc.dl.wf4ever.portal.model.SearchResult;
import pl.psnc.dl.wf4ever.portal.services.SearchService;

import com.sun.syndication.io.FeedException;

public class TestSearch
{

	@BeforeClass
	public static void setUpBeforeClass()
		throws Exception
	{
	}


	@Test
	public final void testFindByKeywords()
		throws IllegalArgumentException, MalformedURLException, FeedException, IOException
	{
		List<SearchResult> ros = SearchService.findByKeywords(
			URI.create("http://sandbox.wf4ever-project.org/opensearch/search.rss"), "riders");
		assertNotNull(ros);
	}

}
