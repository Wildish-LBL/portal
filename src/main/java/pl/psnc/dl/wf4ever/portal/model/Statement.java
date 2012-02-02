/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

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

	private final URI subjectURI;

	private URI propertyURI;

	private String propertyLocalName;

	private boolean isObjectURIResource;

	private final Annotation annotation;

	private String objectValue;

	private URI objectURI;


	public Statement(com.hp.hpl.jena.rdf.model.Statement original, Annotation annotation)
		throws URISyntaxException
	{
		if (original.getSubject().isURIResource()) {
			subjectURI = new URI(original.getSubject().getURI());
		}
		else {
			subjectURI = null;
		}
		setPropertyURI(new URI(original.getPredicate().getURI()));
		RDFNode node = original.getObject();
		isObjectURIResource = node.isURIResource();
		if (isObjectURIResource) {
			objectURI = new URI(node.asResource().getURI());
			objectValue = node.asResource().toString();
		}
		else if (node.isResource()) {
			objectURI = null;
			objectValue = original.getObject().asResource().getId().getLabelString();
		}
		else {
			objectURI = null;
			objectValue = original.getObject().asLiteral().getValue().toString();
		}
		this.annotation = annotation;
	}


	public Statement(URI subjectURI, Annotation annotation)
		throws URISyntaxException
	{
		this.subjectURI = subjectURI;
		setPropertyURI(new URI(DCTerms.title.getURI()));
		isObjectURIResource = false;
		objectURI = null;
		objectValue = "";
		this.annotation = annotation;
	}


	/**
	 * @return the annotation
	 */
	public Annotation getAnnotation()
	{
		return annotation;
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


	public String getPropertyLocalNameNice()
	{
		return splitCamelCase(getPropertyLocalName());
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


	/**
	 * @param propertyURI
	 *            the propertyURI to set
	 */
	public void setPropertyURI(URI propertyURI)
	{
		if (propertyURI == null)
			throw new NullPointerException("Property URI cannot be null");
		this.propertyURI = propertyURI;
		this.propertyLocalName = ModelFactory.createDefaultModel().createProperty(propertyURI.toString())
				.getLocalName();
	}


	/**
	 * @param isObjectURIResource
	 *            the isObjectURIResource to set
	 */
	public void setObjectURIResource(boolean isObjectURIResource)
	{
		this.isObjectURIResource = isObjectURIResource;
	}


	/**
	 * @param objectValue
	 *            the objectValue to set
	 */
	public void setObjectValue(String objectValue)
	{
		this.objectValue = objectValue;
	}


	/**
	 * @param objectURI
	 *            the objectURI to set
	 */
	public void setObjectURI(URI objectURI)
	{
		this.objectURI = subjectURI.resolve(objectURI);
	}


	/**
	 * @return the subjectURI
	 */
	public URI getSubjectURI()
	{
		return subjectURI;
	}


	public com.hp.hpl.jena.rdf.model.Statement createJenaStatement()
	{
		Model model = ModelFactory.createDefaultModel();
		Resource subject = model.createResource(subjectURI.toString());
		Property property = model.createProperty(propertyURI.toString());
		RDFNode object = null;
		if (isObjectURIResource()) {
			object = model.createResource(objectURI.toString());
		}
		else {
			object = model.createTypedLiteral(objectValue);
		}
		return model.createStatement(subject, property, object);
	}


	/*
	 * from
	 * http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human
	 * -readable-names-in-java
	 */
	static String splitCamelCase(String s)
	{
		return s.replaceAll(
			String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"),
			" ");
	}

}
