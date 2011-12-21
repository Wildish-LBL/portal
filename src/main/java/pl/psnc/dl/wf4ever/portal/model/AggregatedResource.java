package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public abstract class AggregatedResource
{

	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");

	protected URI uri;

	protected Calendar created;

	protected String creator;

	protected String name;

	protected long size;

	private Set<Annotation> annotations;


	public AggregatedResource(URI uri, Calendar created, String creator, String name, long size)
	{
		this.uri = uri;
		this.created = created;
		this.creator = creator;
		this.name = name;
		this.size = size;
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
		return SDF.format(getCreated().getTime());
	}


	public String getCreator()
	{
		return creator;
	}


	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * TODO change to resource classes
	 * 
	 * @return
	 */
	public abstract boolean isWorkflow();


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


	public void setAnnotations(Set<Annotation> annotations)
	{
		this.annotations = annotations;
	}


	public Set<Annotation> getAnnotations()
	{
		return this.annotations;
	}


	/**
	 * Adapted from
	 * http://stackoverflow.com/questions/3758606/how-to-convert-byte
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

}