package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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

	protected String creator;

	protected String name;

	protected long size;

	protected String title;

	private List<Annotation> annotations;

	private Type type = Type.OTHER;


	public AggregatedResource(URI uri, Calendar created, String creator, String name, long size, Type type)
	{
		this.uri = uri;
		this.created = created;
		this.creator = creator;
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

}