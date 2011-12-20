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

import org.apache.log4j.Logger;
import org.apache.wicket.request.UrlDecoder;

import pl.psnc.dl.wf4ever.portal.services.OAuthException;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * @author piotrhol
 *
 */
public class ResearchObject
	extends OntModelImpl
	implements Serializable, AggregatedResource
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6525552866849376681L;

	private static final Logger log = Logger.getLogger(ResearchObject.class);

	private static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	private final URI manifestURI;

	private final URI researchObjectURI;

	private final Calendar created;

	private final TreeModel aggregatedResourcesTree;

	private String creator = null;

	private static final Property foafName = ModelFactory.createDefaultModel().createProperty(
		"http://xmlns.com/foaf/0.1/name");


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
		try {
			creator = ro.getPropertyResourceValue(DCTerms.creator).as(Individual.class).getPropertyValue(foafName)
					.asLiteral().getString();
		}
		catch (Exception e) {
			log.warn("RO " + researchObjectURI + " does not define a creator");
		}

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
	@Override
	public URI getURI()
	{
		return researchObjectURI;
	}


	/**
	 * @return the created
	 */
	@Override
	public Calendar getCreated()
	{
		return created;
	}


	private TreeModel createAggregatedResourcesTree(Individual ro)
		throws URISyntaxException
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(this);

		//TODO take care of proxies & folders
		NodeIterator it = listObjectsOfProperty(ro, createProperty(ORE_NAMESPACE + "aggregates"));
		while (it.hasNext()) {
			URI resURI = new URI(it.next().asResource().getURI());
			String name = UrlDecoder.PATH_INSTANCE.decode(researchObjectURI.relativize(resURI).toString(), "UTF-8");
			rootNode.add(new DefaultMutableTreeNode(new RoResource(resURI, name)));
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


	@Override
	public String getName()
	{
		return "RO";
	}


	@Override
	public String toString()
	{
		return getName();
	}


	@Override
	public boolean isWorkflow()
	{
		return false;
	}


	@Override
	public String getCreator()
	{
		return creator;
	}
}
