package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;

public class ResourceGroup
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1544890733112265628L;

	private final String title;

	private final String description;


	/**
	 * @param title
	 * @param description
	 */
	public ResourceGroup(String title, String description)
	{
		this.title = title;
		this.description = description;
	}


	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}


	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}


	@Override
	public String toString()
	{
		return getTitle();
	}

}
