/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;

/**
 * @author piotrhol
 * 
 */
public class SearchResult
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9046167750816266548L;

	private final ResearchObject researchObject;

	private final double score;


	/**
	 * @param researchObject
	 * @param score
	 */
	public SearchResult(ResearchObject researchObject, double score)
	{
		this.researchObject = researchObject;
		this.score = score;
	}


	/**
	 * @return the researchObject
	 */
	public ResearchObject getResearchObject()
	{
		return researchObject;
	}


	/**
	 * @return the score
	 */
	public double getScore()
	{
		return score;
	}


	public int getScoreInPercent()
	{
		return (int) Math.round(score * 100);
	}
}
