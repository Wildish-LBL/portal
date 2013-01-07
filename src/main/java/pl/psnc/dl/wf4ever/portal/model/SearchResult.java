/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;

import org.purl.wf4ever.rosrs.client.ResearchObject;

/**
 * RODL search service result.
 * 
 * @author piotrhol
 * 
 */
public class SearchResult implements Serializable {

    /** id. */
    private static final long serialVersionUID = -9046167750816266548L;

    /** RO found. */
    private final ResearchObject researchObject;

    /** Search score. The higher the better, from 0 to infinity. */
    private final double score;


    /**
     * Constructor.
     * 
     * @param researchObject
     *            RO
     * @param score
     *            search score
     */
    public SearchResult(ResearchObject researchObject, double score) {
        this.researchObject = researchObject;
        this.score = score;
    }


    public ResearchObject getResearchObject() {
        return researchObject;
    }


    public double getScore() {
        return score;
    }


    public int getScoreInPercent() {
        return (int) Math.round(score * 100);
    }
}
