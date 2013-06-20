package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.Annotation;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * A model that is based on a quad (annotation + a triple inside its body). It acts as a model of string, which is the
 * object of the triple. When the model object is loaded, it searches for a value in the annotations. When the value is
 * updated, this model updates the annotations as well.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTripleModel implements IModel<String> {

    /** id. */
    private static final long serialVersionUID = -3397270930648327359L;
    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AnnotationTripleModel.class);

    /** A fixed value indicating that the annotation is unknown, any any matching should be used if exists. */
    public static final Annotation ANY_ANNOTATION = new Annotation(null, null, null, Collections.<URI> emptySet(),
            null, null);

    /** If detached, the value will need to be loaded again. */
    private transient boolean attached = false;

    /** The model for the quad subject. */
    private final IModel<? extends Annotable> annotableModel;

    /** The property. */
    private final URI property;

    /** The annotation containing the triple. */
    private Annotation annotation;

    /** The object = value of the model. */
    private transient String value;


    /**
     * Constructor.
     * 
     * @param annotation
     *            the annotation containing the triple. Use {@link AnnotationTripleModel.ANY_ANNOTATION} if the
     *            annotation is unknown and any existing value is ok.
     * @param annotable
     *            the annotated resource
     * @param property
     *            the property (predicate)
     */
    public AnnotationTripleModel(Annotation annotation, Annotable annotable, URI property) {
        this.annotation = annotation;
        this.annotableModel = new Model<Annotable>(annotable);
        this.property = property;
    }


    /**
     * Constructor.
     * 
     * @param annotation
     *            the annotation containing the triple. Use {@link AnnotationTripleModel.ANY_ANNOTATION} if the
     *            annotation is unknown and any existing value is ok.
     * @param annotableModel
     *            the annotated resource
     * @param property
     *            the property (predicate)
     */
    public AnnotationTripleModel(Annotation annotation, IModel<? extends Annotable> annotableModel, URI property) {
        this.annotation = annotation;
        this.annotableModel = annotableModel;
        this.property = property;
    }


    /**
     * Constructor.
     * 
     * @param annotation
     *            the annotation containing the triple. Use {@link AnnotationTripleModel.ANY_ANNOTATION} if the
     *            annotation is unknown and any existing value is ok.
     * @param annotable
     *            the annotated resource
     * @param property
     *            the property (predicate) as a Jena object
     */
    public AnnotationTripleModel(Annotation annotation, Annotable annotable, Property property) {
        this(annotation, annotable, URI.create(property.getURI()));
    }


    /**
     * Constructor.
     * 
     * @param annotationTriple
     *            the annotation triple
     */
    public AnnotationTripleModel(AnnotationTriple annotationTriple) {
        this.annotation = annotationTriple.getAnnotation();
        this.annotableModel = new Model<Annotable>(annotationTriple.getSubject());
        this.property = annotationTriple.getProperty();
    }


    public AnnotationTriple getAnnotationTriple() {
        return new AnnotationTriple(annotation, annotableModel.getObject(), property, value);
    }


    public IModel<? extends Annotable> getAnnotableModel() {
        return annotableModel;
    }


    @Override
    public void detach() {
        if (attached) {
            attached = false;
            value = null;
        }
    }


    @Override
    public String getObject() {
        if (!attached && annotableModel.getObject() != null && annotableModel.getObject().isLoaded()) {
            attached = true;
            value = load();
        }
        return value;
    }


    @Override
    public void setObject(String object) {
        if (annotableModel.getObject() == null) {
            throw new IllegalStateException("Annotable object cannot be null to set a value");
        }
        if (object != null) {
            if (!object.equals(value)) {
                if (annotation == null || annotation == ANY_ANNOTATION) {
                    try {
                        annotation = annotableModel.getObject().createPropertyValue(property, object);
                        value = object;
                    } catch (ROSRSException | ROException e) {
                        LOG.error("Can't create an annotation", e);
                    }
                } else {
                    try {
                        annotableModel.getObject().updatePropertyValue(annotation, property, object);
                        value = object;
                    } catch (ROSRSException e) {
                        LOG.error("Can't update annotation " + annotation, e);
                    }
                }
                value = object;
            }
        } else if (annotation != null) {
            try {
                annotableModel.getObject().deletePropertyValue(annotation, property);
                value = null;
                annotation = null;
            } catch (ROSRSException e) {
                LOG.error("Can't delete/update annotation " + annotation, e);
            }
        }
        attached = true;
    }


    /**
     * Load the value from the selected annotation or all annotations of the resource.
     * 
     * @return the object as String or null of none found
     */
    private String load() {
        if (annotableModel.getObject() == null) {
            return null;
        }
        Map<Annotation, String> values = annotableModel.getObject().getPropertyValues(property);
        if (annotation == null) {
            return null;
        } else if (annotation == ANY_ANNOTATION) {
            if (!values.isEmpty()) {
                Entry<Annotation, String> e = values.entrySet().iterator().next();
                annotation = e.getKey();
                return e.getValue();
            } else {
                return null;
            }
        } else {
            return values.get(annotation);
        }
    }

}
