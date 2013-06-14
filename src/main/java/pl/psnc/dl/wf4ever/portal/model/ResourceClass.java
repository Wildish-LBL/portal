package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * A type that a user can select for a resource.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceClass implements Serializable {

    /** id. */
    private static final long serialVersionUID = 6561141589132208189L;

    /** URI for saving this resource type in an annotation. */
    private final URI uri;

    /** A human-friendly name for this type. */
    private final String name;

    /** roterms:Conclusions. */
    public static final ResourceClass CONCLUSIONS = new ResourceClass(
            URI.create("http://purl.org/wf4ever/roterms#Conclusions"), "Conclusions");

    /** roterms:Hypothesis. */
    public static final ResourceClass HYPOTHESIS = new ResourceClass(
            URI.create("http://purl.org/wf4ever/roterms#Hypothesis"), "Hypothesis");

    /** roterms:Sketch. */
    public static final ResourceClass SKETCH = new ResourceClass(URI.create("http://purl.org/wf4ever/roterms#Sketch"),
            "Sketch");

    /** wfdesc:Workflow. */
    public static final ResourceClass WORKFLOW = new ResourceClass(
            URI.create("http://purl.org/wf4ever/wfdesc#Workflow"), "Workflow");

    /** All static instances. */
    public static final List<ResourceClass> RESOURCE_CLASSES = Arrays.asList(CONCLUSIONS, HYPOTHESIS, SKETCH, WORKFLOW);


    /**
     * Constructor.
     * 
     * @param uri
     *            URI for saving this resource type in an annotation
     * @param name
     *            A human-friendly name for this type
     */
    public ResourceClass(URI uri, String name) {
        this.uri = uri;
        this.name = name;
    }


    public URI getUri() {
        return uri;
    }


    public String getName() {
        return name;
    }

}
