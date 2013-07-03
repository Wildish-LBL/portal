package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A model that for the predefined {@link ResourceType}.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTypeModel implements IModel<ResourceType> {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ResourceTypeModel.class);
    /** id. */
    private static final long serialVersionUID = -3198087277305620061L;

    /** Resource model. */
    private IModel<? extends Annotable> resourceModel;

    /** Resource used for the last value search. */
    private Annotable resource;

    /** Triple with the last value found. May be null if no value was found. */
    private AnnotationTriple triple;

    /** Resource type. May be null if not found. */
    private ResourceType resourceType;


    /**
     * Constructor.
     * 
     * @param resourceModel
     *            resource model
     */
    public ResourceTypeModel(IModel<? extends Annotable> resourceModel) {
        this.resourceModel = resourceModel;
    }


    @Override
    public void detach() {
    }


    @Override
    public ResourceType getObject() {
        if (resourceModel.getObject() != null && !resourceModel.getObject().equals(resource)) {
            resource = resourceModel.getObject();
            ResourceType resourceType2 = null;
            AnnotationTriple triple2 = null;
            List<AnnotationTriple> types = resource.getPropertyValues(RDF.type, false);
            for (AnnotationTriple t : types) {
                try {
                    URI typeUri = new URI(t.getValue());
                    resourceType2 = ResourceType.forUri(typeUri);
                    if (resourceType2 != null) {
                        triple2 = t;
                        break;
                    }
                } catch (URISyntaxException e) {
                    LOG.debug("A type is not a URI: " + e.getMessage());
                }
            }
            triple = triple2;
            resourceType = resourceType2;
        }
        return resourceType;
    }


    @Override
    public void setObject(ResourceType type) {
        resource = resourceModel.getObject();
        if (triple != null) {
            if (type != null) {
                try {
                    triple.updateValue(type.getUri().toString());
                    resourceType = type;
                } catch (ROSRSException e) {
                    LOG.error("Can't update annotation " + triple.getAnnotation(), e);
                }
            } else {
                try {
                    triple.delete();
                    triple = null;
                    resourceType = null;
                } catch (ROSRSException e) {
                    LOG.error("Can't delete annotation " + triple.getAnnotation(), e);
                }
            }
        } else {
            if (type != null) {
                try {
                    triple = resource.createPropertyValue(RDF.type, type.getUri());
                    resourceType = type;
                } catch (ROSRSException | ROException e) {
                    LOG.error("Can't create annotation " + triple.getAnnotation(), e);
                }
            }
        }
    }


    public IModel<? extends Annotable> getResourceModel() {
        return resourceModel;
    }
}
