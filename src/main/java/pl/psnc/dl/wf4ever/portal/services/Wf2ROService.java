package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

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


    /**
     * Launches the Wf-RO transformation service and waits until it's finished.
     * 
     * @param wf2ROService
     *            Wf-RO service URI
     * @param workflow
     *            workflow URI
     * @param string
     *            workflow MIME type
     * @param ro
     *            RO URI
     * @param accessToken
     *            RODL access token
     * @return {@link JobStatus} after the job has finished
     * @throws IOException
     *             when there is a problem reported by the transformation service
     */
    public static JobStatus transformWorkflow(URI wf2ROService, URI workflow, String string, URI ro, String accessToken)
            throws IOException {
        JobConfig config = new JobConfig(workflow, string, ro, accessToken);
        Client client = Client.create();
        WebResource webResource = client.resource(wf2ROService.toString());

        ClientResponse response = webResource.path("jobs").type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, config);
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
        if (status.getStatus() != State.DONE) {
            throw new IOException("Transformation finished with status " + status.getStatus());
        }
        return status;
    }
}
