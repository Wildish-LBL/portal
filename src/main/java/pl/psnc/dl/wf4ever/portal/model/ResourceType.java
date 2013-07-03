package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * A type that a user can select for a resource.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceType implements Serializable {

    /** id. */
    private static final long serialVersionUID = 6561141589132208189L;

    /** URI for saving this resource type in an annotation. */
    private final URI uri;

    /** A human-friendly name for this type. */
    private final String name;

    /** A longer explanation of this type. */
    private final String definition;

    /** roterms:Conclusions. */
    public static final ResourceType CONCLUSIONS = new ResourceType(
            URI.create("http://purl.org/wf4ever/roterms#Conclusions"), "Conclusions");

    /** roterms:Hypothesis. */
    public static final ResourceType HYPOTHESIS = new ResourceType(
            URI.create("http://purl.org/wf4ever/roterms#Hypothesis"), "Hypothesis");

    /** roterms:Sketch. */
    public static final ResourceType SKETCH = new ResourceType(URI.create("http://purl.org/wf4ever/roterms#Sketch"),
            "Sketch");
    /** roterms:Results. */
    public static final ResourceType RESULTS = new ResourceType(URI.create("http://purl.org/wf4ever/roterms#Results"),
            "Results");

    /** roterms:ResultsPresentation. */
    public static final ResourceType RESULTS_PRESENTATION = new ResourceType(
            URI.create("http://purl.org/wf4ever/roterms#ResultsPresentation"), "Results Presentation");

    /** roterms:ExampleInput. */
    public static final ResourceType EXAMPLE_INPUT = new ResourceType(
            URI.create("http://purl.org/wf4ever/roterms#ExampleInput"), "Example Input");

    /** roterms:ExampleOutput. */
    public static final ResourceType EXAMPLE_OUTPUT = new ResourceType(
            URI.create("http://purl.org/wf4ever/roterms#ExampleOutput"), "Example Output");

    /** roterms:WorkflowRunBundle. */
    public static final ResourceType WORKFLOW_RUN_BUNDLE = new ResourceType(
            URI.create("http://purl.org/wf4ever/roterms#WorkflowRunBundle"), "Workflow Run Bundle");

    /** dcterms:BibliographicResource. */
    public static final ResourceType BIBLIOGRAPHIC_RESOURCE = new ResourceType(URI.create(DCTerms.BibliographicResource
            .getURI()), "Bibliographic Resource", "A book, article, or other documentary resource.");

    /** wfdesc:Workflow. */
    public static final ResourceType WORKFLOW = new ResourceType(URI.create("http://purl.org/wf4ever/wfdesc#Workflow"),
            "Workflow");

    /** wfdesc:Process. */
    public static final ResourceType WORKFLOW_PROCESS = new ResourceType(
            URI.create("http://purl.org/wf4ever/wfdesc#Process"),
            "Workflow Process",
            "A class of actions that when enacted give rise to processes. A process can have 0 or more input and output parameters, signifying what kind of parameters the process will require and return.");

    /** wfever:Dataset. */
    public static final ResourceType DATASET = new ResourceType(URI.create("http://purl.org/wf4ever/wf4ever#Dataset"),
            "Dataset");

    /** wfever:Document. */
    public static final ResourceType DOCUMENT = new ResourceType(
            URI.create("http://purl.org/wf4ever/wf4ever#Document"), "Document");

    /** wfever:Image. */
    public static final ResourceType IMAGE = new ResourceType(URI.create("http://purl.org/wf4ever/wf4ever#Image"),
            "Image");

    /** wfever:File. */
    public static final ResourceType FILE = new ResourceType(URI.create("http://purl.org/wf4ever/wf4ever#File"), "File");

    /** wfprov:WorkflowRun. */
    public static final ResourceType WORKFLOW_RUN = new ResourceType(
            URI.create("http://purl.org/wf4ever/wfprov#WorkflowRun"),
            "Workflow Run",
            "A workflow run is a process run which has been enacted by a workflow engine, according to a workflow definition. Such a process typically contains several subprocesses corresponding to process descriptions.");

    /** All static instances. */
    public static final List<ResourceType> VALUES = Arrays.asList(WORKFLOW, WORKFLOW_PROCESS, DATASET, DOCUMENT, IMAGE,
        FILE, WORKFLOW_RUN, SKETCH, HYPOTHESIS, CONCLUSIONS, RESULTS, RESULTS_PRESENTATION, EXAMPLE_INPUT,
        EXAMPLE_OUTPUT, WORKFLOW_RUN_BUNDLE, BIBLIOGRAPHIC_RESOURCE);


    /**
     * Constructor.
     * 
     * @param uri
     *            URI for saving this resource type in an annotation
     * @param name
     *            A human-friendly name for this type
     */
    public ResourceType(URI uri, String name) {
        this(uri, name, null);
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
    public ResourceType(URI uri, String name, String definition) {
        this.uri = uri;
        this.name = name;
        this.definition = definition;
    }


    public URI getUri() {
        return uri;
    }


    public String getName() {
        return name;
    }


    public String getDefinition() {
        return definition;
    }


    /**
     * Find the resource type with the given URI.
     * 
     * @param uri
     *            URI
     * @return a resource type or null
     */
    public static ResourceType forUri(URI uri) {
        for (ResourceType type : ResourceType.VALUES) {
            if (type.getUri().equals(uri)) {
                return type;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return getName();
    }

}
