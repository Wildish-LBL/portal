/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;
import org.purl.wf4ever.rosrs.client.common.AnonId;

import pl.psnc.dl.wf4ever.portal.services.RoFactory;

/**
 * Represents ao:Annotation.
 * 
 * @author piotrhol
 * 
 */
public class Annotation extends AggregatedResource {

    /** id. */
    private static final long serialVersionUID = 8821418401311175036L;

    /** Annotation body URI. */
    private URI bodyURI;

    /** Jena annotation body model (not serializable). */
    private LoadableDetachableModel<List<Statement>> bodyModel;


    /**
     * Constructor.
     * 
     * @param uri
     *            annotation URI
     * @param created
     *            creation date
     * @param creators
     *            list of creators
     * @param name
     *            annotation name
     * @param bodyURI
     *            annotation body URI
     */
    public Annotation(URI uri, Calendar created, List<Creator> creators, String name, URI bodyURI) {
        super(uri, created, creators, name);
        this.bodyURI = bodyURI;
        bodyModel = new AnnotationBodyModel();
    }


    /**
     * Constructor.
     * 
     * @param id
     *            blank node id
     * @param created
     *            creation date
     * @param creators
     *            list of creators
     * @param name
     *            annotation name
     * @param bodyURI
     *            annotation body URI
     */
    public Annotation(AnonId id, Calendar created, List<Creator> creators, String name, URI bodyURI) {
        super(id, created, creators, name);
        this.bodyURI = bodyURI;
        bodyModel = new AnnotationBodyModel();
    }


    public URI getBodyURI() {
        return bodyURI;
    }


    /*
     * (non-Javadoc)
     * 
     * @see pl.psnc.dl.wf4ever.portal.model.AggregatedResource#getDownloadURI()
     */
    @Override
    public URI getDownloadURI() {
        return getURI();
    }


    @Override
    public String getSizeFormatted() {
        return "--";
    }


    public List<Statement> getBody() {
        return bodyModel.getObject();
    }


    /**
     * Not-serializable model of an annotation body.
     * 
     * @author piotrekhol
     * 
     */
    private final class AnnotationBodyModel extends LoadableDetachableModel<List<Statement>> {

        /** id. */
        private static final long serialVersionUID = 4142916952621994965L;


        @Override
        protected List<Statement> load() {
            try {
                List<Statement> res = RoFactory.createAnnotationBody(Annotation.this, getBodyURI());
                return res != null ? res : new ArrayList<Statement>();
            } catch (URISyntaxException e) {
                return new ArrayList<Statement>();
            }
        }
    }
}
