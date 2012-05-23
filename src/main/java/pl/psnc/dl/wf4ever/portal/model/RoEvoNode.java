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
 * @author piotrekhol
 * 
 */
public class RoEvoNode
	implements Serializable
{

	private static final long serialVersionUID = 3083098588758012143L;

	public enum EvoClass {
		LIVE, SNAPSHOT, ARCHIVED, UNKNOWN
	}

	private URI uri;

	/**
	 * rdfs:label
	 */
	private String label;

	/**
	 * Does it have ro:ResearchObject RDF class
	 */
	private boolean isResearchObject = false;

	/**
	 * Any of roevo RDF classes
	 */
	private EvoClass evoClass = EvoClass.UNKNOWN;

	/**
	 * For snapshots: roevo:previousSnapshot
	 */
	private final List<RoEvoNode> previousSnapshots = new ArrayList<>();

	/**
	 * For snapshots: roevo:hasSnapshot
	 */
	private final List<RoEvoNode> itsLiveROs = new ArrayList<>();

	/**
	 * For snapshots: roevo:derivedFrom
	 */
	private final List<RoEvoNode> derivedResources = new ArrayList<>();

	/**
	 * For live ROs: roevo:derivedFrom
	 */
	private final List<RoEvoNode> sourceResources = new ArrayList<>();

	/**
	 * Its representation on the page
	 */
	private Component component;


	public RoEvoNode()
	{

	}


	public RoEvoNode(URI uri)
	{
		setUri(uri);
	}


	public URI getUri()
	{
		return uri;
	}


	public void setUri(URI uri)
	{
		this.uri = uri;
	}


	public String getLabel()
	{
		return label;
	}


	public void setLabel(String identifier)
	{
		this.label = identifier;
	}


	public String getLabelOrIdentifier()
	{
		if (getLabel() != null) {
			return getLabel();
		}
		else if (getUri() != null) {
			String[] segments = getUri().getPath().split("/");
			return segments[segments.length - 1];
		}
		return null;
	}


	public EvoClass getEvoClass()
	{
		return evoClass;
	}


	public void setEvoClass(EvoClass evoClass)
	{
		this.evoClass = evoClass;
	}


	public boolean isResearchObject()
	{
		return isResearchObject;
	}


	public void setResearchObject(boolean isResearchObject)
	{
		this.isResearchObject = isResearchObject;
	}


	public List<RoEvoNode> getPreviousSnapshots()
	{
		return previousSnapshots;
	}


	public List<RoEvoNode> getItsLiveROs()
	{
		return itsLiveROs;
	}


	public List<RoEvoNode> getDerivedResources()
	{
		return derivedResources;
	}


	public List<RoEvoNode> getSourceResources()
	{
		return sourceResources;
	}


	public Component getComponent()
	{
		return component;
	}


	public void setComponent(Component component)
	{
		this.component = component;
	}

}
