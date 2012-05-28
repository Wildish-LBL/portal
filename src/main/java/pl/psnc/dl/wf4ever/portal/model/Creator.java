/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;

import org.apache.log4j.Logger;
import org.purl.wf4ever.rosrs.client.common.ROSRService;
import org.purl.wf4ever.rosrs.client.common.Vocab;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Represents dcterms:creator.
 * 
 * @author piotrhol
 * 
 */
public class Creator implements Serializable {

    /** id. */
    private static final long serialVersionUID = 4104896753518461266L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(Creator.class);

    /** Creator name or URI. */
    private String value;

    /** Creator URI, or null. */
    private URI uri;

    /** Is the creator name being fetched from its URI. */
    private boolean isLoading;


    /**
     * Constructor, will NOT try to load the name from the URI.
     * 
     * @param value
     *            name as string
     */
    public Creator(String value) {
        this.value = value;
        isLoading = false;
    }


    /**
     * Constructor, will try to load the name from the URI.
     * 
     * @param rodlURI
     *            RODL URI
     * @param uri
     *            creator URI
     */
    public Creator(final URI rodlURI, final URI uri) {
        this.uri = uri;
        this.value = uri.toString();
        isLoading = true;

        new Thread() {

            public void run() {
                try {
                    // 3. FOAF data under user URI
                    OntModel userModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
                    userModel.read(uri.toString(), null);
                    Resource r2 = userModel.createResource(uri.toString());
                    if (r2 != null && r2.hasProperty(Vocab.foafName)) {
                        setValue(r2.as(Individual.class).getPropertyValue(Vocab.foafName).asLiteral().getString());
                        isLoading = false;
                    }
                } catch (Exception e) {
                    LOG.debug("No FOAF data under user URI: " + e.getMessage());
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


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public URI getURI() {
        return uri;
    }


    public boolean isLoading() {
        return isLoading;
    }


    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }


    @Override
    public String toString() {
        return getValue();
    }

}
