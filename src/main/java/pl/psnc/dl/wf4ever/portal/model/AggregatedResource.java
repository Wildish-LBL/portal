package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ocpsoft.pretty.time.PrettyTime;

public abstract class AggregatedResource
	implements Serializable
{

	public enum Type {
		WORKFLOW, WEB_SERVICE, OTHER, RESEARCH_OBJECT, ANNOTATION
	};

	private static final long serialVersionUID = -472666872267555742L;

	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

	protected URI uri;

	protected Calendar created;

	protected List<Creator> creators;

	protected String name;

	protected long size;

	protected String title;

	private List<Annotation> annotations;

	private Type type = Type.OTHER;

	private Multimap<String, AggregatedResource> relations = HashMultimap.create();

	private double stability = -1;

	private URI provenanceTraceURI;

	private Set<ResourceGroup> matchingGroups = new HashSet<>();


	public AggregatedResource(URI uri, Calendar created, List<Creator> creators, String name, long size, Type type)
	{
		this.uri = uri;
		this.created = created;
		this.creators = creators;
		this.name = name;
		this.size = size;
		this.setType(type);
	}


	public AggregatedResource()
	{
	}


	/**
	 * @return the researchObjectURI
	 */
	public URI getURI()
	{
		return uri;
	}


	/**
	 * @return the download URI
	 */
	public abstract URI getDownloadURI();


	/**
	 * @return the created
	 */
	public Calendar getCreated()
	{
		return created;
	}


	/**
	 * @return the created
	 */
	public String getCreatedFormatted()
	{
		if (getCreated() != null)
			return SDF.format(getCreated().getTime());
		else
			return null;
	}


	/**
	 * @return the created
	 */
	public String getCreatedAgoFormatted()
	{
		if (getCreated() != null)
			return new PrettyTime().format(getCreated().getTime());
		else
			return null;
	}


	public List<Creator> getCreators()
	{
		return creators;
	}


	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}


	@Override
	public String toString()
	{
		return getName();
	}


	/**
	 * @return the size, nicely formatted (i.e. 23 MB)
	 */
	public long getSize()
	{
		return size;
	}


	/**
	 * @return the size, nicely formatted (i.e. 23 MB)
	 */
	public String getSizeFormatted()
	{
		return humanReadableByteCount(getSize());
	}


	public void setAnnotations(List<Annotation> annotations)
	{
		this.annotations = annotations;
	}


	public List<Annotation> getAnnotations()
	{
		return this.annotations;
	}


	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}


	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}


	/**
	 * Adapted from http://stackoverflow.com/questions/3758606/how-to-convert-byte
	 * -size-into-human-readable-format-in-java
	 * 
	 * @param bytes
	 * @param si
	 * @return
	 */
	private static String humanReadableByteCount(long bytes)
	{
		int unit = 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		return String.format("%.1f %cB", bytes / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
	}


	/**
	 * @return the type
	 */
	public Type getType()
	{
		return type;
	}


	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(Type type)
	{
		this.type = type;
	}


	/**
	 * @return the relations
	 */
	public Multimap<String, AggregatedResource> getRelations()
	{
		return relations;
	}


	/**
	 * @param relations
	 *            the relations to set
	 */
	public void setRelations(Multimap<String, AggregatedResource> relations)
	{
		this.relations = relations;
	}


	public ArrayList<Entry<String, AggregatedResource>> getRelationsEntries()
	{
		return new ArrayList<Entry<String, AggregatedResource>>(relations.entries());
	}


	/**
	 * @return the stability
	 */
	public double getStability()
	{
		return Math.round(stability);
	}


	/**
	 * @param stability
	 *            the stability to set
	 */
	public void setStability(double stability)
	{
		this.stability = stability;
	}


	public void setProvenanceTraceURI(URI provenanceTraceURI)
	{
		this.provenanceTraceURI = provenanceTraceURI;
	}


	/**
	 * @return the provenanceTraceURI
	 */
	public URI getProvenanceTraceURI()
	{
		return provenanceTraceURI;
	}


	public Set<ResourceGroup> getMatchingGroups()
	{
		return matchingGroups;
	}

}
