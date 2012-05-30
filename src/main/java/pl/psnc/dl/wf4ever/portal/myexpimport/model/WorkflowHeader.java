/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Short myExperiment workflow metadata.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "workflow")
public class WorkflowHeader extends BaseResourceHeader {

    /** id. */
    private static final long serialVersionUID = 3019438116219497825L;


    @Override
    public URI getResourceUrl() {
        return URI.create(getUri().toString() + "&elements=content-uri,content-type,id,title");
    }


    @Override
    public Class<Workflow> getResourceClass() {
        return Workflow.class;
    }
}
