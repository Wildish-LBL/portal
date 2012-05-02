package pl.psnc.dl.wf4ever.portal.model;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Vocab
{

	static final String RO_NAMESPACE = "http://purl.org/wf4ever/ro#";

	static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	static final String AO_NAMESPACE = "http://purl.org/ao/";

	static final String WFPROV_NAMESPACE = "http://purl.org/wf4ever/wfprov#";

	static final String WFDESC_NAMESPACE = "http://purl.org/wf4ever/wfdesc#";

	static final String WF4EVER_NAMESPACE = "http://purl.org/wf4ever/wf4ever#";

	public static final OntModel model = (OntModel) ModelFactory
			.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RDFS_INF).read(WFDESC_NAMESPACE);

	public static final Resource roResource = ModelFactory.createDefaultModel().createResource(
		"http://purl.org/wf4ever/ro#Resource");

	public static final Resource aggregatedAnnotation = ModelFactory.createDefaultModel().createResource(
		"http://purl.org/wf4ever/ro#AggregatedAnnotation");

	public static final Resource foafAgent = ModelFactory.createDefaultModel().createResource(
		"http://xmlns.com/foaf/0.1/Agent");

	public static final Property foafName = ModelFactory.createDefaultModel().createProperty(
		"http://xmlns.com/foaf/0.1/name");

	public static final Property foafPrimaryTopic = ModelFactory.createDefaultModel().createProperty(
		"http://xmlns.com/foaf/0.1/primaryTopic");

	public static final Property filesize = ModelFactory.createDefaultModel().createProperty(
		"http://purl.org/wf4ever/ro#filesize");

	public static final Property aggregates = ModelFactory.createDefaultModel().createProperty(
		Vocab.ORE_NAMESPACE + "aggregates");

	public static final Property annotatesAggregatedResource = ModelFactory.createDefaultModel().createProperty(
		Vocab.RO_NAMESPACE + "annotatesAggregatedResource");

	public static final Property aoBody = ModelFactory.createDefaultModel().createProperty(Vocab.AO_NAMESPACE + "body");

	public static final OntProperty hasSubProcess = model.createOntProperty(Vocab.WFDESC_NAMESPACE + "hasSubProcess");
}
