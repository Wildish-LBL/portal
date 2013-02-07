/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.purl.wf4ever.rosrs.client.ResearchObject;

/**
 * A node in visualization of RO evolution. Represents an RO or any other resource.
 * 
 * @author piotrekhol
 * 
 */
public class RoEvoNode implements Serializable {

    /** id. */
    private static final long serialVersionUID = 3083098588758012143L;

    /** Resource URI. */
    private ResearchObject researchObject;

    /** rdfs:label. */
    private String label;

    /** Does it have ro:ResearchObject RDF class. */
    private boolean isResearchObject = true;

    /** Its representation on the page. */
    private Component component;

    /** position from the left of the panel, starting with 0. */
    private int index;


    /**
     * Default constructor.
     */
    public RoEvoNode() {

    }


    /**
     * Constructor.
     * 
     * @param uri
     *            resource URI
     */
    public RoEvoNode(ResearchObject researchObject) {
        setResearchObject(researchObject);
    }


    public ResearchObject getResearchObject() {
        return researchObject;
    }


    public void setResearchObject(ResearchObject researchObject) {
        this.researchObject = researchObject;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String identifier) {
        this.label = identifier;
    }


    /**
     * Returns label or, if it's null, the identifier.
     * 
     * @return label or identifier
     */
    public String getLabelOrIdentifier() {
        if (getLabel() != null) {
            return getLabel();
        } else if (getResearchObject() != null) {
            return getResearchObject().getName();
        }
        return null;
    }


    public boolean isResearchObject() {
        return isResearchObject;
    }


    public void setResearchObject(boolean isResearchObject) {
        this.isResearchObject = isResearchObject;
    }


    public Component getComponent() {
        return component;
    }


    public void setComponent(Component component) {
        this.component = component;
    }


    public int getIndex() {
        return index;
    }


    public void setIndex(int index) {
        this.index = index;
    }

}
