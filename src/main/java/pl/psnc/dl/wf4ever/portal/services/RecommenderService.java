/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import pl.psnc.dl.wf4ever.portal.model.Recommendation;
import pl.psnc.dl.wf4ever.portal.model.Recommendations;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author piotrhol
 * 
 */
public class RecommenderService
{

	public static List<Recommendation> getRecommendations(URI baseURI, String myExpId, int limit)
		throws Exception
	{
		Client client = Client.create();
		WebResource webResource = client.resource(baseURI.toString()).path("recommendation").path("user").path(myExpId)
				.queryParam("max", "" + limit);
		ClientResponse response = webResource.get(ClientResponse.class);
		if (response.getStatus() != HttpServletResponse.SC_OK) {
			throw new Exception("Wrong response status: " + response.getClientResponseStatus());
		}

		Recommendations recs = response.getEntity(Recommendations.class);
		return recs.getRecommendations();
	}

}
