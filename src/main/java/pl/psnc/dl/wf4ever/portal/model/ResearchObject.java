/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import pl.psnc.dl.wf4ever.portal.services.OAuthException;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * @author piotrhol
 *
 */
public class ResearchObject
	extends OntModelImpl
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6525552866849376681L;

	private static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	private final URI manifestURI;

	private final URI researchObjectURI;

	private final Calendar created;


	public ResearchObject(URI baseURI)
		throws OAuthException, URISyntaxException
	{
		super(OntModelSpec.OWL_LITE_MEM);
		manifestURI = baseURI.resolve(".ro/manifest");
		this.read(manifestURI.toString());

		Individual manifest = getIndividual(manifestURI.toString());
		Individual ro = manifest.getPropertyResourceValue(createProperty(ORE_NAMESPACE + "describes")).as(
			Individual.class);
		researchObjectURI = new URI(ro.getURI());
		created = ((XSDDateTime) ro.getPropertyValue(DCTerms.created).asLiteral().getValue()).asCalendar();
	}


	/**
	 * @return the manifestURI
	 */
	public URI getManifestURI()
	{
		return manifestURI;
	}


	/**
	 * @return the researchObjectURI
	 */
	public URI getResearchObjectURI()
	{
		return researchObjectURI;
	}


	/**
	 * @return the created
	 */
	public Calendar getCreated()
	{
		return created;
	}
}
