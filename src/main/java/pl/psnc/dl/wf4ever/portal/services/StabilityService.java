package pl.psnc.dl.wf4ever.portal.services;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import pl.psnc.dl.wf4ever.portal.model.Stability;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class StabilityService {

    public static double calculateStability(URI baseURI, URI traceURI)
            throws Exception {
        Client client = Client.create();
        WebResource webResource = client.resource(baseURI.toString()).path("rest/stability")
                .queryParam("ROprov", traceURI.toString() + "?content=true");
        ClientResponse response = webResource.get(ClientResponse.class);
        if (response.getStatus() != HttpServletResponse.SC_OK) {
            throw new Exception("Wrong response status: " + response.getClientResponseStatus());
        }

        Stability stability = response.getEntity(Stability.class);

        return stability.getStabilityValue();
    }

}
