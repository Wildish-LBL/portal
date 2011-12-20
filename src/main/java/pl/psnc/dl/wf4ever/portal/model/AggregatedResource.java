package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.Calendar;

public interface AggregatedResource
{

	/**
	 * @return the resource URI
	 */
	public URI getURI();


	/**
	 * @return the name
	 */
	public String getName();


	/**
	 * TODO change to resource classes
	 * @return
	 */
	public boolean isWorkflow();


	/**
	 * @return the created
	 */
	public Calendar getCreated();


	/**
	 * @return the created
	 */
	public String getCreator();
}