package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class ResourceGroup
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1544890733112265628L;

	private final String title;

	private String description;

	private final Set<URI> rdfClasses = new HashSet<>();


	/**
	 * @param title
	 * @param description
	 */
	public ResourceGroup(String title)
	{
		this.title = title;
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


	public void setDescription(String description)
	{
		this.description = description;
	}


	@Override
	public String toString()
	{
		return getTitle();
	}


	public Set<URI> getRdfClasses()
	{
		return rdfClasses;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceGroup other = (ResourceGroup) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		return true;
	}

}
