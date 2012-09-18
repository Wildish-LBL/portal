package pl.psnc.dl.wf4ever.portal;

import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import pl.psnc.dl.wf4ever.portal.model.Recommendation;
import pl.psnc.dl.wf4ever.portal.services.RecommenderService;

/**
 * Test the recommendation service.
 * 
 * @author piotrekhol
 * 
 */
public class TestRecommender {

    /**
     * Does it return anything?
     * 
     * @throws Exception
     *             any error
     */
    @Test
    @Ignore
    public final void testRecommend()
            throws Exception {
        List<Recommendation> recs = RecommenderService.getRecommendations(
            URI.create("http://sandbox.wf4ever-project.org/epnoiServer/rest/recommendations/"), "2", 2);
        assertNotNull(recs);
    }
}
