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
	public abstract String getSize();
}