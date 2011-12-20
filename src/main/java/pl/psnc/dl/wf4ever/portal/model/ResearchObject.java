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
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * @author piotrhol
 *
 */
public class ResearchObject
	extends AggregatedResource
	implements Serializable
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

	private static final Property filesize = ModelFactory.createDefaultModel().createProperty(
		"http://purl.org/wf4ever/ro#filesize");


	public ResearchObject(URI baseURI)
		throws OAuthException, URISyntaxException
	{
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);

		manifestURI = baseURI.resolve(".ro/manifest");
		model.read(manifestURI.toString());

		Individual manifest = model.getIndividual(manifestURI.toString());
		Individual ro = manifest.getPropertyResourceValue(model.createProperty(ORE_NAMESPACE + "describes")).as(
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

		this.aggregatedResourcesTree = createAggregatedResourcesTree(ro, model);
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


	private TreeModel createAggregatedResourcesTree(Individual ro, OntModel model)
		throws URISyntaxException
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(this);

		//TODO take care of proxies & folders
		NodeIterator it = model.listObjectsOfProperty(ro, model.createProperty(ORE_NAMESPACE + "aggregates"));
		while (it.hasNext()) {
			URI resURI = new URI(it.next().asResource().getURI());
			rootNode.add(new DefaultMutableTreeNode(createResource(resURI, model)));
		}
		return new DefaultTreeModel(rootNode);
	}


	/**
	 * @param rootNode
	 * @param resURI
	 */
	private RoResource createResource(URI resURI, OntModel model)
	{
		Individual res = model.getIndividual(resURI.toString());
		Calendar rCreated = null;
		String rCreator = null;
		long rSize = 0;

		try {
			rCreated = ((XSDDateTime) res.getPropertyValue(DCTerms.created).asLiteral().getValue()).asCalendar();
		}
		catch (Exception e) {
		}
		try {
			creator = res.getPropertyResourceValue(DCTerms.creator).as(Individual.class).getPropertyValue(foafName)
					.asLiteral().getString();
		}
		catch (Exception e) {
		}
		try {
			rSize = res.getPropertyValue(filesize).asLiteral().getLong();
		}
		catch (Exception e) {
		}

		String name = UrlDecoder.PATH_INSTANCE.decode(researchObjectURI.relativize(resURI).toString(), "UTF-8");
		return new RoResource(resURI, name, rCreator, rCreated, rSize);
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
	public boolean isWorkflow()
	{
		return false;
	}


	@Override
	public String getCreator()
	{
		return creator;
	}


	@Override
	public String getSize()
	{
		return "--";
	}

}
