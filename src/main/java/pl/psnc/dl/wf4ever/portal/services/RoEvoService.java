/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pl.psnc.dl.wf4ever.portal.model.RoEvoNode;
import pl.psnc.dl.wf4ever.portal.model.Vocab;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * @author piotrekhol
 * 
 */
public class RoEvoService
{

	public Collection<RoEvoNode> describeSnapshot(URI sparqlEndpointURI, URI researchObjectURI)
		throws IOException, URISyntaxException
	{
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		QueryExecution x = QueryExecutionFactory.sparqlService(sparqlEndpointURI.toString(),
			MyQueryFactory.getSnapshotEvolution(researchObjectURI.toString()));
		x.execConstruct(model);

		Map<URI, RoEvoNode> nodes = new HashMap<>();
		StmtIterator it = model.listStatements();
		while (it.hasNext()) {
			Statement statement = it.next();
			URI subjectURI = new URI(statement.getSubject().getURI());
			if (!nodes.containsKey(subjectURI)) {
				nodes.put(subjectURI, new RoEvoNode(subjectURI));
			}
			RoEvoNode node = nodes.get(subjectURI);
			Property property = statement.getPredicate();
			RDFNode object = statement.getObject();
			if (property.equals(DCTerms.identifier)) {
				node.setIdentifier(object.asLiteral().getString());
			}
			else if (property.equals(Vocab.isSnapshotOf) && object.isURIResource()) {
				node.getItsLiveROs().add(createNode(nodes, object.asResource().getURI()));
			}
			else if (property.equals(Vocab.hasPreviousVersion) && object.isURIResource()) {
				node.getPreviousSnapshots().add(createNode(nodes, object.asResource().getURI()));
			}
			else if (property.equals(Vocab.derivedFrom) && object.isURIResource()) {
				node.getDerivedResources().add(createNode(nodes, object.asResource().getURI()));
			}
		}
		return nodes.values();
	}


	private RoEvoNode createNode(Map<URI, RoEvoNode> nodes, String objectURIString)
		throws URISyntaxException
	{
		URI objectURI = new URI(objectURIString);
		if (!nodes.containsKey(objectURI)) {
			nodes.put(objectURI, new RoEvoNode(objectURI));
		}
		return nodes.get(objectURI);
	}
}
