package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents ro:AggregatedResource.
 * 
 * @author piotrekhol
 * 
 */
public class AggregatedResource implements Serializable {

    /** id. */
    private static final long serialVersionUID = -472666872267555742L;

    /** Date format: Tuesday 14:03. */
    public static final SimpleDateFormat SDF1 = new SimpleDateFormat("EEEE HH:mm");

    /** Date format: 24 05 2012 23:04. */
    public static final SimpleDateFormat SDF2 = new SimpleDateFormat("dd MMMM yyyy HH:mm");

    /** Resource URI. */
    protected URI uri;

    /** Creation date. */
    protected Calendar created;

    /** List of creator URIs. */
    protected List<Creator> creators;

    /** Resource id. */
    protected String name;

    /** Resource size in bytes. */
    protected long size = -1;

    /** Resource title, optional. */
    protected String title;

    /** Annotations annotating this resource. */
    private List<Annotation> annotations;

    /** Relations to other aggregated resources. */
    private Multimap<String, AggregatedResource> relations = HashMultimap.create();

    /** The opposite of relations, used for updating relations. */
    private Multimap<String, AggregatedResource> inverseRelations = HashMultimap.create();

    /** Stability of resource, unused. */
    private double stability = -1;

    /** URI of a provenance trace resource for measuring stability, unused. */
    private URI provenanceTraceURI;

    /** Resource groups to which this resource belongs. */
    private Set<ResourceGroup> matchingGroups = new HashSet<>();


    /**
     * Constructor.
     * 
     * @param uri
     *            resource URI
     * @param created
     *            creation date
     * @param creators
     *            list of resource creators
     * @param name
     *            resource nice name
     */
    public AggregatedResource(URI uri, Calendar created, List<Creator> creators, String name) {
        this.uri = uri;
        this.created = created;
        this.creators = creators;
        this.name = name;
    }


    /**
     * Default constructor.
     */
    public AggregatedResource() {
    }


    public URI getURI() {
        return uri;
    }


    /**
     * URI under which the resource can be downloaded. This is currently a hack, works only for RODL internal resource.
     * 
     * @return URI
     */
    public URI getDownloadURI() {
        //FIXME hack
        if (uri.toString().startsWith("http://sandbox.wf4ever-project.org/rosrs5/")) {
            try {
                return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), "content=true", null);
            } catch (URISyntaxException e) {
                return null;
            }
        } else {
            return null;
        }
    }


    public Calendar getCreated() {
        return created;
    }


    /**
     * Creation date formatted. If the date is less than a week ago, the day/hour format is used, otherwise date/hour.
     * 
     * @return the created nicely formatted date
     */
    public String getCreatedFormatted() {
        if (getCreated() != null) {
            if (new DateTime(getCreated()).compareTo(new DateTime().minusWeeks(1)) > 0) {
                return SDF1.format(getCreated().getTime());
            } else {
                return SDF2.format(getCreated().getTime());
            }

        } else {
            return null;
        }
    }


    public List<Creator> getCreators() {
        return creators;
    }


    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return getName();
    }


    /**
     * Resource size in bytes.
     * 
     * @return the size in bytes
     */
    public long getSize() {
        return size;
    }


    public void setSize(long size) {
        this.size = size;
    }


    /**
     * Resource size nicely formatted.
     * 
     * @return the size, nicely formatted (i.e. 23 MB)
     */
    public String getSizeFormatted() {
        if (getSize() >= 0) {
            return humanReadableByteCount(getSize());
        } else {
            return null;
        }
    }


    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }


    public List<Annotation> getAnnotations() {
        return this.annotations;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Adapted from http://stackoverflow.com/questions/3758606/how-to-convert-byte
     * -size-into-human-readable-format-in-java.
     * 
     * @param bytes
     *            size in bytes
     * @return nicely formatted size
     */
    private static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        return String.format("%.1f %cB", bytes / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }


    public Multimap<String, AggregatedResource> getRelations() {
        return relations;
    }


    public Multimap<String, AggregatedResource> getInverseRelations() {
        return inverseRelations;
    }


    public ArrayList<Entry<String, AggregatedResource>> getRelationsEntries() {
        return new ArrayList<Entry<String, AggregatedResource>>(relations.entries());
    }


    public ArrayList<String> getRelationKeys() {
        return new ArrayList<String>(relations.keySet());
    }


    public double getStability() {
        return Math.round(stability);
    }


    public void setStability(double stability) {
        this.stability = stability;
    }


    public void setProvenanceTraceURI(URI provenanceTraceURI) {
        this.provenanceTraceURI = provenanceTraceURI;
    }


    public URI getProvenanceTraceURI() {
        return provenanceTraceURI;
    }


    public Set<ResourceGroup> getMatchingGroups() {
        return matchingGroups;
    }

}
