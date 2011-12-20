package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class AggregatedResource
{

	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");


	/**
	 * @return the resource URI
	 */
	public abstract URI getURI();


	/**
	 * @return the download URI
	 */
	public abstract URI getDownloadURI();


	/**
	 * @return the name
	 */
	public abstract String getName();


	/**
	 * TODO change to resource classes
	 * @return
	 */
	public abstract boolean isWorkflow();


	/**
	 * @return the created
	 */
	public abstract Calendar getCreated();


	/**
	 * @return the created
	 */
	public String getCreatedFormatted()
	{
		return SDF.format(getCreated().getTime());
	}


	@Override
	public String toString()
	{
		return getName();
	}


	/**
	 * @return the created
	 */
	public abstract String getCreator();


	/**
	 * @return the size, nicely formatted (i.e. 23 MB)
	 */
	public abstract long getSize();


	/**
	 * @return the size, nicely formatted (i.e. 23 MB)
	 */
	public String getSizeFormatted()
	{
		return humanReadableByteCount(getSize());
	}


	/**
	 * Adapted from http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
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