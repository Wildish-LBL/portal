/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * @author piotrhol
 *
 */
public class RoFactory
{

	private static final Logger log = Logger.getLogger(RoFactory.class);

	private static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	private final OntModel model;

	private final URI manifestURI;

	private final URI researchObjectURI;

	private final Individual ro;

	private static final Resource roResource = ModelFactory.createDefaultModel().createResource(
		"http://purl.org/wf4ever/ro#Resource");

	private static final Property foafName = ModelFactory.createDefaultModel().createProperty(
		"http://xmlns.com/foaf/0.1/name");

	private static final Property filesize = ModelFactory.createDefaultModel().createProperty(
		"http://purl.org/wf4ever/ro#filesize");

	private static final Property aggregates = ModelFactory.createDefaultModel().createProperty(
		ORE_NAMESPACE + "aggregates");


	public RoFactory(URI baseURI)
		throws OAuthException, URISyntaxException
	{
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);

		manifestURI = baseURI.resolve(".ro/manifest");
		model.read(manifestURI.toString());

		Individual manifest = model.getIndividual(manifestURI.toString());
		ro = manifest.getPropertyResourceValue(model.createProperty(ORE_NAMESPACE + "describes")).as(Individual.class);
		researchObjectURI = new URI(ro.getURI());
	}


	public ResearchObject createResearchObject()
		throws URISyntaxException
	{

		Calendar created = null;
		String creator = null;
		try {
			created = ((XSDDateTime) ro.getPropertyValue(DCTerms.created).asLiteral().getValue()).asCalendar();
		}
		catch (Exception e) {
			log.warn("RO " + researchObjectURI + " does not define a creator");
		}
		try {
			creator = ro.getPropertyResourceValue(DCTerms.creator).as(Individual.class).getPropertyValue(foafName)
					.asLiteral().getString();
		}
		catch (Exception e) {
			log.warn("RO " + researchObjectURI + " does not define a creator");
		}
		return new ResearchObject(researchObjectURI, created, creator);
	}


	public TreeModel createAggregatedResourcesTree(ResearchObject researchObject)
		throws URISyntaxException
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(researchObject);

		//TODO take care of proxies & folders
		NodeIterator it = model.listObjectsOfProperty(ro, aggregates);
		while (it.hasNext()) {
			Individual res = it.next().as(Individual.class);
			if (res.hasRDFType(roResource)) {
				rootNode.add(new DefaultMutableTreeNode(createResource(new URI(res.getURI()))));
			}
		}
		return new DefaultTreeModel(rootNode);
	}


	/**
	 * @param rootNode
	 * @param resourceURI
	 */
	public RoResource createResource(URI resourceURI)
	{
		Individual res = model.getIndividual(resourceURI.toString());
		Calendar created = null;
		String creator = null;
		long size = 0;

		try {
			created = ((XSDDateTime) res.getPropertyValue(DCTerms.created).asLiteral().getValue()).asCalendar();
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
			size = res.getPropertyValue(filesize).asLiteral().getLong();
		}
		catch (Exception e) {
		}

		String name = UrlDecoder.PATH_INSTANCE.decode(researchObjectURI.relativize(resourceURI).toString(), "UTF-8");
		return new RoResource(resourceURI, name, creator, created, size);
	}

}
