package pl.psnc.dl.wf4ever.portal;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import pl.psnc.dl.wf4ever.portal.model.ResearchObject;
import pl.psnc.dl.wf4ever.portal.services.SearchService;

public class TestSearch
{

	@BeforeClass
	public static void setUpBeforeClass()
		throws Exception
	{
	}


	@Test
	public final void testFindByKeywords()
	{
		List<ResearchObject> ros = SearchService.findByKeywords(Arrays.asList("Jingle", "bells"));
		assertNotNull(ros);
	}

}
