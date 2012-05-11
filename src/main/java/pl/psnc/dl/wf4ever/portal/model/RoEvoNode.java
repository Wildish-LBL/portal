/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author piotrekhol
 * 
 */
public class RoEvoNode
	implements Serializable
{

	private static final long serialVersionUID = 3083098588758012143L;

	private URI uri;

	/**
	 * dcterms:identifier
	 */
	private String identifier;

	/**
	 * Does it have ro:ResearchObject RDF class
	 */
	private boolean isResearchObject;

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


	public String getIdentifier()
	{
		return identifier;
	}


	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
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

}
