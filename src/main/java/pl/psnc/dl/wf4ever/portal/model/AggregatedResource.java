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

import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class AggregatedResource
	implements Serializable
{

	private static final long serialVersionUID = -472666872267555742L;

	public static final SimpleDateFormat SDF1 = new SimpleDateFormat("EEEE HH:mm");

	public static final SimpleDateFormat SDF2 = new SimpleDateFormat("dd MMMM yyyy HH:mm");

	protected URI uri;

	protected URI downloadURI;

	protected Calendar created;

	protected List<Creator> creators;

	protected String name;

	protected long size = -1;

	protected String title;

	private List<Annotation> annotations;

	private Multimap<String, AggregatedResource> relations = HashMultimap.create();

	private Multimap<String, AggregatedResource> inverseRelations = HashMultimap.create();

	private double stability = -1;

	private URI provenanceTraceURI;

	private Set<ResourceGroup> matchingGroups = new HashSet<>();


	public AggregatedResource(URI uri, Calendar created, List<Creator> creators, String name)
	{
		this.uri = uri;
		this.created = created;
		this.creators = creators;
		this.name = name;
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


	public URI getDownloadURI()
	{
		return downloadURI;
	}


	public void setDownloadURI(URI downloadURI)
	{
		this.downloadURI = downloadURI;
	}


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
		if (getCreated() != null) {
			if (new DateTime(getCreated()).compareTo(new DateTime().minusWeeks(1)) > 0) {
				return SDF1.format(getCreated().getTime());
			}
			else {
				return SDF2.format(getCreated().getTime());
			}

		}
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


	public void setSize(long size)
	{
		this.size = size;
	}


	/**
	 * @return the size, nicely formatted (i.e. 23 MB)
	 */
	public String getSizeFormatted()
	{
		if (getSize() >= 0)
			return humanReadableByteCount(getSize());
		else
			return null;
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
	 * @return the relations
	 */
	public Multimap<String, AggregatedResource> getRelations()
	{
		return relations;
	}


	/**
	 * @return the relations
	 */
	public Multimap<String, AggregatedResource> getInverseRelations()
	{
		return inverseRelations;
	}


	public ArrayList<Entry<String, AggregatedResource>> getRelationsEntries()
	{
		return new ArrayList<Entry<String, AggregatedResource>>(relations.entries());
	}


	public ArrayList<Entry<String, AggregatedResource>> getInverseRelationsEntries()
	{
		return new ArrayList<Entry<String, AggregatedResource>>(inverseRelations.entries());
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
