package pl.psnc.dl.wf4ever.portal.model;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.Annotation;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.Resource;
import org.purl.wf4ever.rosrs.client.Statement;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.vocabulary.WFPROV;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * A type that a user can select for a resource.
 * 
 * @author piotrekhol
 * 
 */
public class WorkflowRunResourceType extends ResourceType {

    /** id. */
    private static final long serialVersionUID = 6561141589132208189L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(WorkflowRunResourceType.class);


    /**
     * Constructor.
     * 
     * @param uri
     *            URI for saving this resource type in an annotation
     * @param name
     *            A human-friendly name for this type
     */
    public WorkflowRunResourceType(URI uri, String name) {
        super(uri, name);
    }


    /**
     * Constructor.
     * 
     * @param uri
     *            URI for saving this resource type in an annotation
     * @param name
     *            A human-friendly name for this type
     * @param definition
     *            A longer explanation of this type
     */
    public WorkflowRunResourceType(URI uri, String name, String definition) {
        super(uri, name, definition);
    }


    @Override
    public Collection<? extends AnnotationTriple> getRelatedAnnotationTriples(Annotable resource,
            Collection<AnnotationTriple> directTriples) {
        Set<AnnotationTriple> triples = new HashSet<>(directTriples);
        Set<Individual> workflowRuns = new HashSet<>();
        for (Annotation annotation : resource.getAnnotations()) {
            try {
                if (!annotation.isLoaded()) {
                    annotation.load();
                }
                workflowRuns.addAll(findWorkflowRuns(annotation.getBodyAsModel()));
            } catch (ROSRSException e) {
                LOG.error("Can't load annotation", e);
            }
        }
        for (Annotation annotation : resource.getAnnotations()) {
            if (annotation.isLoaded()) {
                Model model = annotation.getBodyAsModel();
                for (Individual runR : workflowRuns) {
                    if (model.contains(runR, RDF.type, model.createResource(getUri().toString()))) {
                        // we add a fake resource to represent the workflow run
                        // it should work for removing only
                        Annotable run = new Resource(null, URI.create(runR.asResource().getURI()), null, null, null);
                        triples.add(new AnnotationTriple(annotation, run, RDF.type, getUri().toString(), false));
                    }
                }
            }
        }
        return triples;
    }


    @Override
    public Collection<? extends Statement> getRelatedStatements(Annotable resource) {
        Set<Statement> statements = new HashSet<>();
        statements.addAll(super.getRelatedStatements(resource));
        for (Annotation annotation : resource.getAnnotations()) {
            try {
                if (!annotation.isLoaded()) {
                    annotation.load();
                }
                Model model = annotation.getBodyAsModel();
                Collection<Individual> workflowRuns = findWorkflowRuns(model);
                for (Individual runR : workflowRuns) {
                    statements.add(new Statement(URI.create(runR.getURI()), URI.create(RDF.type.getURI()), getUri()
                            .toString()));
                }
            } catch (ROSRSException e) {
                LOG.error("Can't load annotation", e);
            }
        }
        return statements;
    }


    /**
     * Find all workflow runs in the Jena model.
     * 
     * @param model
     *            Jena model
     * @return a collection of Jena individuals
     */
    private Collection<Individual> findWorkflowRuns(Model model) {
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
        ontModel.add(model);
        return ontModel.listIndividuals(WFPROV.WorkflowRun).toSet();
    }

}
