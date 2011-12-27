/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * @author piotrhol
 *
 */
public class Statement
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1704407898614509230L;

	private final URI propertyURI;

	private final String propertyLocalName;

	private final boolean isObjectURIResource;

	private final String objectValue;

	private final URI objectURI;


	public Statement(com.hp.hpl.jena.rdf.model.Statement original)
		throws URISyntaxException
	{
		Property property = original.getPredicate();
		propertyURI = new URI(property.getURI());
		propertyLocalName = property.getLocalName();
		RDFNode node = original.getObject();
		isObjectURIResource = node.isURIResource();
		if (isObjectURIResource) {
			objectURI = new URI(node.asResource().getURI());
			objectValue = node.asResource().toString();
		}
		else {
			objectURI = null;
			objectValue = original.getObject().asLiteral().getValue().toString();
		}
	}


	/**
	 * @return the propertyURI
	 */
	public URI getPropertyURI()
	{
		return propertyURI;
	}


	/**
	 * @return the propertyLocalName
	 */
	public String getPropertyLocalName()
	{
		return propertyLocalName;
	}


	/**
	 * @return the objectValue
	 */
	public String getObjectValue()
	{
		return objectValue;
	}


	/**
	 * @return the objectURI
	 */
	public URI getObjectURI()
	{
		return objectURI;
	}


	/**
	 * @return the isObjectURIResource
	 */
	public boolean isObjectURIResource()
	{
		return isObjectURIResource;
	}

}
