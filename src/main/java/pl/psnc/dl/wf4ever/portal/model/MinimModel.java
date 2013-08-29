package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;

/**
 * Represents a URI of the minim model for the checklist evaluation service, together with a user-friendly name and
 * description.
 * 
 * @author piotrekhol
 * 
 */
public class MinimModel implements Serializable {

    /** id. */
    private static final long serialVersionUID = 1468797861471863789L;

    /** Minim model URI. */
    private final URI uri;

    /**
     * The purpose against which the RO should be evaluated. If the minim model has more than 1 purpose, you need to
     * create multiple instances of this class with the same URI and different purposes (and probably different
     * title/description).
     */
    private final String purpose;

    /**
     * A short title.
     */
    private final String title;

    /** A longer description. */
    private final String description;


    /**
     * Constructor.
     * 
     * @param uri
     *            Minim model URI
     * @param purpose
     *            Evaluation purpose
     * @param title
     *            A short title
     * @param description
     *            A longer description
     */
    public MinimModel(URI uri, String purpose, String title, String description) {
        this.uri = uri;
        this.purpose = purpose;
        this.title = title;
        this.description = description;
    }


    public URI getUri() {
        return uri;
    }


    public String getPurpose() {
        return purpose;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }

}
