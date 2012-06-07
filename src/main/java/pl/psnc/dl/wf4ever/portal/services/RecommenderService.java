/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import pl.psnc.dl.wf4ever.portal.model.Recommendation;
import pl.psnc.dl.wf4ever.portal.model.Recommendations;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Recommender service API.
 * 
 * @author piotrhol
 * 
 */
public final class RecommenderService {

    /**
     * Constructor.
     */
    private RecommenderService() {
        //nope
    }


    /**
     * Retrieve annotations for a myExperiment user.
     * 
     * @param baseURI
     *            recommender service URI
     * @param myExpId
     *            myExperiment user id
     * @param limit
     *            limit of recommendations
     * @return a list of {@link Recommendation}
     * @throws IOException
     *             the service returned an error
     */
    public static List<Recommendation> getRecommendations(URI baseURI, String myExpId, int limit)
            throws IOException {
        Client client = Client.create();
        WebResource webResource = client.resource(baseURI.toString()).path("recommendation").path("user").path(myExpId)
                .queryParam("max", "" + limit);
        ClientResponse response = webResource.get(ClientResponse.class);
        if (response.getStatus() != HttpServletResponse.SC_OK) {
            throw new IOException("Wrong response status: " + response.getClientResponseStatus());
        }

        Recommendations recs = response.getEntity(Recommendations.class);
        return recs.getRecommendations();
    }

}
