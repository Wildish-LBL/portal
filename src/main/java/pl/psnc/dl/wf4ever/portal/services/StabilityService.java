package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import pl.psnc.dl.wf4ever.portal.model.Stability;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Stability service API.
 * 
 * @author piotrekhol
 * 
 */
public final class StabilityService {

    /**
     * Constructor.
     */
    private StabilityService() {
        // nope
    }


    /**
     * Call the stability service.
     * 
     * @param baseURI
     *            stability service URI
     * @param traceURI
     *            provenance trace URI
     * @return some value
     * @throws IOException
     *             can't connect to the stability service
     */
    public static double calculateStability(URI baseURI, URI traceURI)
            throws IOException {
        Client client = Client.create();
        WebResource webResource = client.resource(baseURI.toString()).path("rest/stability")
                .queryParam("ROprov", traceURI.toString() + "?content=true");
        ClientResponse response = webResource.get(ClientResponse.class);
        if (response.getStatus() != HttpServletResponse.SC_OK) {
            throw new IOException("Wrong response status: " + response.getClientResponseStatus());
        }

        Stability stability = response.getEntity(Stability.class);

        return stability.getStabilityValue();
    }

}
