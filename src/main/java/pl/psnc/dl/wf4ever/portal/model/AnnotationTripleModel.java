package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.List;

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
public class AnnotationTripleModel implements IModel<AnnotationTriple> {

    /** id. */
    private static final long serialVersionUID = -3397270930648327359L;
    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AnnotationTripleModel.class);

    /** The model for the quad subject. */
    private final IModel<? extends Annotable> annotableModel;

    /** The property. */
    private URI property;

    /** The annotation containing the triple. */
    private Annotation annotation;

    /** The triple value. It's always a String, even if it represents a URI resource. */
    private String value;

    /** Use any existing matching annotation triple. */
    private final boolean anyExisting;

    /** Model for updating only the triple value. */
    private final ValueModel valueModel = new ValueModel();


    /**
     * Constructor.
     * 
     * @param triple
     *            annotation triple
     */
    public AnnotationTripleModel(AnnotationTriple triple) {
        this.annotableModel = new Model<>(triple.getSubject());
        this.property = triple.getProperty();
        this.annotation = triple.getAnnotation();
        this.value = triple.getValue();
        this.anyExisting = false;
    }


    /**
     * Constructor.
     * 
     * @param annotableModel
     *            the annotated resource
     * @param property
     *            the immutable property
     * @param anyExisting
     *            should any existing value be searched
     */
    public AnnotationTripleModel(IModel<? extends Annotable> annotableModel, URI property, boolean anyExisting) {
        this.annotableModel = annotableModel;
        this.property = property;
        this.anyExisting = anyExisting;
    }


    /**
     * Constructor.
     * 
     * @param annotableModel
     *            the annotated resource
     * @param property
     *            the immutable property
     * @param anyExisting
     *            should any existing value be searched
     */
    public AnnotationTripleModel(IModel<? extends Annotable> annotableModel, Property property, boolean anyExisting) {
        this(annotableModel, URI.create(property.getURI()), anyExisting);
    }


    public IModel<? extends Annotable> getAnnotableModel() {
        return annotableModel;
    }


    @Override
    public void detach() {
    }


    @Override
    public AnnotationTriple getObject() {
        return getAnnotationTriple();
    }


    private AnnotationTriple getAnnotationTriple() {
        return annotation != null ? new AnnotationTriple(annotation, annotableModel.getObject(), property, value,
                anyExisting) : null;
    }


    @Override
    public void setObject(AnnotationTriple newTriple) {
        if (newTriple.getProperty() == null || newTriple.getValue() == null) {
            delete();
        } else {
            AnnotationTriple triple = getAnnotationTriple();
            if (triple != null) {
                try {
                    triple.updatePropertyValue(newTriple.getProperty(), newTriple.getValue());
                    property = newTriple.getProperty();
                    value = newTriple.getValue();
                } catch (ROSRSException e) {
                    LOG.error("Can't update annotation " + annotation, e);
                }
            }
        }
    }


    /**
     * Delete this annotation triple.
     */
    public void delete() {
        AnnotationTriple triple = getAnnotationTriple();
        if (triple != null) {
            try {
                triple.delete();
                value = null;
                annotation = null;
            } catch (ROSRSException e) {
                LOG.error("Can't delete/update annotation " + annotation, e);
            }
        }
    }


    public ValueModel getValueModel() {
        return valueModel;
    }


    /**
     * A model for retrieving and updating only the triple value. Useful for simple components that allow to edit only
     * the value.
     * 
     * @author piotrekhol
     * 
     */
    class ValueModel implements IModel<String> {

        /** id. */
        private static final long serialVersionUID = -7572730556806614174L;


        @Override
        public void detach() {
        }


        @Override
        public String getObject() {
            if (annotableModel.getObject() == null) {
                return null;
            }
            if (value == null && anyExisting) {
                List<AnnotationTriple> triples = annotableModel.getObject().getPropertyValues(property, true);
                if (!triples.isEmpty()) {
                    annotation = triples.get(0).getAnnotation();
                    value = triples.get(0).getValue();
                }
            }
            return value;
        }


        @Override
        public void setObject(String object) {
            if (annotableModel.getObject() == null) {
                throw new IllegalStateException("Annotable object cannot be null to set a value");
            }
            AnnotationTriple triple = getAnnotationTriple();
            if (object != null) {
                if (!object.equals(value)) {
                    if (triple == null) {
                        try {
                            triple = annotableModel.getObject().createPropertyValue(property, object);
                            annotation = triple.getAnnotation();
                            value = object;
                        } catch (ROSRSException | ROException e) {
                            LOG.error("Can't create an annotation", e);
                        }
                    } else {
                        try {
                            triple.updateValue(object);
                            value = object;
                        } catch (ROSRSException e) {
                            LOG.error("Can't update annotation " + annotation, e);
                        }
                    }
                    value = object;
                }
            } else if (triple != null) {
                try {
                    triple.delete();
                    value = null;
                    annotation = null;
                } catch (ROSRSException e) {
                    LOG.error("Can't delete/update annotation " + annotation, e);
                }
            }
        }
    }

}
