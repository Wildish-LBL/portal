/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;

/**
 * A node in visualization of RO evolution. Represents an RO or any other resource.
 * 
 * @author piotrekhol
 * 
 */
public class RoEvoNode implements Serializable {

    /** id. */
    private static final long serialVersionUID = 3083098588758012143L;


    /**
     * roevo RDF class.
     * 
     * @author piotrekhol
     * 
     */
    public enum EvoClass {
        /** roevo:LiveRO. */
        LIVE,
        /** roevo:SnapshotRO. */
        SNAPSHOT,
        /** roevo:ArchivedRO. */
        ARCHIVED,
        /** Default value. */
        UNKNOWN
    }


    /**
     * Modifiers suggesting that the node may need to be placed not according to its EvoClass.
     * 
     * @author piotrekhol
     * 
     */
    public enum EvoClassModifier {
        /** Some other node is roevo:derivedFrom this one. */
        SOURCE,
        /** This node is roevo:derivedFrom some other node. */
        FORK,
        /** Default value. */
        NONE
    }


    /** Resource URI. */
    private URI uri;

    /** rdfs:label. */
    private String label;

    /** Does it have ro:ResearchObject RDF class. */
    private boolean isResearchObject = false;

    /** Any of roevo RDF classes. */
    private EvoClass evoClass = EvoClass.UNKNOWN;

    /** Position modifiers. */
    private EvoClassModifier evoLayer = EvoClassModifier.NONE;

    /** For snapshots: roevo:previousSnapshot. */
    private final List<RoEvoNode> previousSnapshots = new ArrayList<>();

    /** For snapshots: roevo:hasSnapshot. */
    private final List<RoEvoNode> itsLiveROs = new ArrayList<>();

    /** For snapshots: roevo:derivedFrom. */
    private final List<RoEvoNode> derivedResources = new ArrayList<>();

    /** For live ROs: roevo:derivedFrom. */
    private final List<RoEvoNode> sourceResources = new ArrayList<>();

    /** Its representation on the page. */
    private Component component;


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
    public RoEvoNode(URI uri) {
        setUri(uri);
    }


    public URI getUri() {
        return uri;
    }


    public void setUri(URI uri) {
        this.uri = uri;
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
        } else if (getUri() != null) {
            String[] segments = getUri().getPath().split("/");
            return segments[segments.length - 1];
        }
        return null;
    }


    public EvoClass getEvoClass() {
        return evoClass;
    }


    public void setEvoClass(EvoClass evoClass) {
        this.evoClass = evoClass;
    }


    public EvoClassModifier getEvoClassModifier() {
        return evoLayer;
    }


    public void setEvoClassModifier(EvoClassModifier evoLayer) {
        this.evoLayer = evoLayer;
    }


    public boolean isResearchObject() {
        return isResearchObject;
    }


    public void setResearchObject(boolean isResearchObject) {
        this.isResearchObject = isResearchObject;
    }


    public List<RoEvoNode> getPreviousSnapshots() {
        return previousSnapshots;
    }


    public List<RoEvoNode> getItsLiveROs() {
        return itsLiveROs;
    }


    public List<RoEvoNode> getDerivedResources() {
        return derivedResources;
    }


    public List<RoEvoNode> getSourceResources() {
        return sourceResources;
    }


    public Component getComponent() {
        return component;
    }


    public void setComponent(Component component) {
        this.component = component;
    }

}
