/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * myExperiment pack complete metadata.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "pack")
public class Pack extends BaseResource {

    /** id. */
    private static final long serialVersionUID = 3146768347985843474L;

    /** Internal pack resource. */
    private List<InternalPackItemHeader> resources = new ArrayList<InternalPackItemHeader>();


    @XmlElementWrapper(name = "internal-pack-items")
    @XmlElements({ @XmlElement(name = "file", type = InternalPackItemHeader.class),
            @XmlElement(name = "workflow", type = InternalPackItemHeader.class) })
    public List<InternalPackItemHeader> getResources() {
        return resources;
    }


    public void setResources(List<InternalPackItemHeader> resources) {
        this.resources = resources;
    }


    @Override
    public String toString() {
        return String.format("pack \"%s\"", getId());
    }

}
