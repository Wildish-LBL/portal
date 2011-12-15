/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.InputStream;
import java.net.URI;

import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author piotrhol
 *
 */
public class Manifest
	extends OntModelImpl
{

	public static Manifest create(URI researchObjectURI)
		throws OAuthException
	{
		InputStream is = ROSRService.getResource(researchObjectURI.resolve(".ro/manifest"));
		return null;
	}


	public Manifest(OntModelSpec spec, Model model)
	{
		super(spec, model);
		// TODO Auto-generated constructor stub
	}

}
