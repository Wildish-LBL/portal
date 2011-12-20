/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import pl.psnc.dl.wf4ever.portal.services.OAuthException;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * @author piotrhol
 *
 */
public class ResearchObject
	extends OntModelImpl
	implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6525552866849376681L;

	private static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	private final URI manifestURI;

	private final URI researchObjectURI;

	private final Calendar created;

	private final TreeModel aggregatedResourcesTree;


	public ResearchObject(URI baseURI)
		throws OAuthException, URISyntaxException
	{
		super(OntModelSpec.OWL_LITE_MEM);
		manifestURI = baseURI.resolve(".ro/manifest");
		this.read(manifestURI.toString());

		Individual manifest = getIndividual(manifestURI.toString());
		Individual ro = manifest.getPropertyResourceValue(createProperty(ORE_NAMESPACE + "describes")).as(
			Individual.class);
		researchObjectURI = new URI(ro.getURI());
		created = ((XSDDateTime) ro.getPropertyValue(DCTerms.created).asLiteral().getValue()).asCalendar();

		this.aggregatedResourcesTree = createAggregatedResourcesTree(ro);
	}


	/**
	 * @return the manifestURI
	 */
	public URI getManifestURI()
	{
		return manifestURI;
	}


	/**
	 * @return the researchObjectURI
	 */
	public URI getResearchObjectURI()
	{
		return researchObjectURI;
	}


	/**
	 * @return the created
	 */
	public Calendar getCreated()
	{
		return created;
	}


	public TreeModel createAggregatedResourcesTree(Individual ro)
		throws URISyntaxException
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new AggregatedResource(researchObjectURI, "RO"));

		//TODO take care of proxies & folders
		NodeIterator it = listObjectsOfProperty(ro, createProperty(ORE_NAMESPACE + "aggregates"));
		while (it.hasNext()) {
			URI resURI = new URI(it.next().asResource().getURI());
			String name = researchObjectURI.relativize(resURI).toString();
			rootNode.add(new DefaultMutableTreeNode(new AggregatedResource(resURI, name)));
		}
		return new DefaultTreeModel(rootNode);
	}


	/**
	 * @return the aggregatedResourcesTree
	 */
	public TreeModel getAggregatedResourcesTree()
	{
		return aggregatedResourcesTree;
	}
}
