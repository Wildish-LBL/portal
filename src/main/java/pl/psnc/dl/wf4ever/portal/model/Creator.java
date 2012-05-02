/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;

import org.apache.log4j.Logger;

import pl.psnc.dl.wf4ever.portal.services.ROSRService;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author piotrhol
 * 
 */
public class Creator
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4104896753518461266L;

	private static final Logger log = Logger.getLogger(Creator.class);

	private String value;

	private boolean isLoading;


	public Creator(String value)
	{
		this.value = value;
		isLoading = false;
	}


	public Creator(final URI rodlURI, final URI uri)
	{
		this.value = uri.toString();
		isLoading = true;

		new Thread() {

			public void run()
			{
				try {
					// 3. FOAF data under user URI
					OntModel userModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
					userModel.read(uri.toString(), null);
					Resource r2 = userModel.createResource(uri.toString());
					if (r2 != null && r2.hasProperty(Vocab.foafName)) {
						setValue(r2.as(Individual.class).getPropertyValue(Vocab.foafName).asLiteral().getString());
						isLoading = false;
					}
				}
				catch (Exception e) {
					log.debug("No FOAF data under user URI: " + e.getMessage());
				}
				if (isLoading) {
					// 4. FOAF data in RODL
					OntModel userModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
					userModel.read(ROSRService.getUser(rodlURI, uri), null);
					Resource r2 = userModel.createResource(uri.toString());
					if (r2 != null && r2.hasProperty(Vocab.foafName)) {
						setValue(r2.as(Individual.class).getPropertyValue(Vocab.foafName).asLiteral().getString());
						isLoading = false;
					}
				}
				synchronized (Creator.this) {
					Creator.this.notifyAll();
				}
			};
		}.start();

	}


	public String getValue()
	{
		return value;
	}


	public void setValue(String value)
	{
		this.value = value;
	}


	public boolean isLoading()
	{
		return isLoading;
	}


	public void setLoading(boolean isLoading)
	{
		this.isLoading = isLoading;
	}


	@Override
	public String toString()
	{
		return getValue();
	}

}
