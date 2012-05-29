package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import pl.psnc.dl.wf4ever.portal.model.JobConfig;
import pl.psnc.dl.wf4ever.portal.model.JobStatus;
import pl.psnc.dl.wf4ever.portal.model.JobStatus.State;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Implements the API of a Workflow-RO transformation service. See
 * http://www.wf4ever-project.org/wiki/display/docs/Wf-RO+transformation+service.
 * 
 * @author piotrekhol
 * 
 */
public final class Wf2ROService {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(Wf2ROService.class);


    /**
     * Private constructor.
     */
    private Wf2ROService() {
        // nope
    }


    public static void transformWorkflow(URI wf2ROService, URI workflow, URI workflowFormat, URI ro, String accessToken)
            throws IOException {
        JobConfig config = new JobConfig(workflow, workflowFormat, ro, accessToken);
        Client client = Client.create();
        WebResource webResource = client.resource(wf2ROService.toString());

        ClientResponse response = webResource.path("jobs").post(ClientResponse.class, config);
        if (response.getStatus() != HttpServletResponse.SC_CREATED) {
            throw new IOException("Service returned response " + response.toString());
        }
        if (response.getLocation() == null) {
            throw new IOException("Service has not returned a location URI");
        }
        URI job = response.getLocation();
        JobStatus status = webResource.uri(job).get(JobStatus.class);
        while (status.getStatus() == State.RUNNING) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOG.error("Sleep interrupted", e);
            }
            status = webResource.uri(job).get(JobStatus.class);
        }

    }

}
