/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Internal myExperiment pack item. Such resource basically references another item such as pack, workflow or file.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@XmlRootElement(name = "internal-pack-item")
public class InternalPackItem extends BaseResource {

    /** id. */
    private static final long serialVersionUID = -9038815722609845400L;

    /** A list of references to other resources. */
    private List<BaseResourceHeader> items;


    /**
     * Default constructor.
     */
    public InternalPackItem() {

    }


    /**
     * Returns the first item from the list.
     * 
     * @return the item the first resource or null
     */
    public BaseResourceHeader getItem() {
        if (items == null || items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }


    /**
     * Returns all the references resources.
     * 
     * @return the items a list of workflow or file headers
     */
    @XmlElementWrapper(name = "item")
    @XmlElements({ @XmlElement(name = "file", type = FileHeader.class),
            @XmlElement(name = "workflow", type = WorkflowHeader.class) })
    public List<BaseResourceHeader> getItems() {
        return items;
    }


    public void setItems(List<BaseResourceHeader> items) {
        this.items = items;
    }
}
