package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.Statement;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A model that for the predefined {@link ResourceType}.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTypeModel implements IModel<Collection<ResourceType>> {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ResourceTypeModel.class);
    /** id. */
    private static final long serialVersionUID = -3198087277305620061L;

    /** Resource model. */
    private IModel<? extends Annotable> resourceModel;

    /** Resource used for the last value search. */
    private Annotable resource;

    /** Resource type. May be empty if not found. */
    private Multimap<ResourceType, AnnotationTriple> resourceTypes = HashMultimap.create();


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
    public Collection<ResourceType> getObject() {
        if (resourceModel.getObject() != null && !resourceModel.getObject().equals(resource)) {
            resource = resourceModel.getObject();
            resourceTypes.clear();
            List<AnnotationTriple> typeTriples = resource.getPropertyValues(RDF.type, false);
            for (AnnotationTriple triple : typeTriples) {
                try {
                    URI typeUri = new URI(triple.getValue());
                    ResourceType type = ResourceType.forUri(typeUri);
                    if (type != null) {
                        resourceTypes.put(type, triple);
                    }
                } catch (URISyntaxException e) {
                    LOG.warn("A type is not a URI: " + e.getMessage());
                }
            }
        }
        // it seems that Wicket will attempt to take this collection, 
        // modify it and return using setObject(). For this to work, it cannot
        // be the Multiset#keySet value
        return new HashSet<>(resourceTypes.keySet());
    }


    @Override
    public void setObject(Collection<ResourceType> newTypes) {
        resource = resourceModel.getObject();
        Set<ResourceType> oldTypes = resourceTypes.keySet();
        Set<ResourceType> typesToRemove = new HashSet<>(oldTypes);
        typesToRemove.removeAll(newTypes);
        Set<ResourceType> typesToAdd = new HashSet<>(newTypes);
        typesToAdd.removeAll(oldTypes);

        Set<AnnotationTriple> triplesToRemove = new HashSet<>();
        for (ResourceType type : typesToRemove) {
            triplesToRemove.addAll(type.getRelatedAnnotationTriples(resource, resourceTypes.removeAll(type)));
        }
        Set<Statement> statementsToAdd = new HashSet<>();
        for (ResourceType type : typesToAdd) {
            statementsToAdd.addAll(type.getRelatedStatements(resource));
        }
        try {
            AnnotationTriple.batchRemove(triplesToRemove);
            Set<AnnotationTriple> newTriples = AnnotationTriple.batchAdd(resource, statementsToAdd);
            for (AnnotationTriple triple : newTriples) {
                try {
                    URI typeUri = new URI(triple.getValue());
                    ResourceType type = ResourceType.forUri(typeUri);
                    if (type != null) {
                        resourceTypes.put(type, triple);
                    }
                } catch (URISyntaxException e) {
                    LOG.warn("A type is not a URI: " + e.getMessage());
                }
            }
        } catch (ROSRSException | ROException e1) {
            LOG.error("Failed to update resource types", e1);
        }
    }


    public IModel<? extends Annotable> getResourceModel() {
        return resourceModel;
    }
}
