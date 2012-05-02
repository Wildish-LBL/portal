package pl.psnc.dl.wf4ever.portal.model;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class Vocab
{

	static final String RO_NAMESPACE = "http://purl.org/wf4ever/ro#";

	static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	static final String AO_NAMESPACE = "http://purl.org/ao/";

	static final String WFPROV_NAMESPACE = "http://purl.org/wf4ever/wfprov#";

	static final String WFDESC_NAMESPACE = "http://purl.org/wf4ever/wfdesc#";

	static final String WF4EVER_NAMESPACE = "http://purl.org/wf4ever/wf4ever#";

	public static final OntModel model = (OntModel) ModelFactory
			.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RDFS_INF).read(RO_NAMESPACE).read(ORE_NAMESPACE)
			.read(AO_NAMESPACE).read(WFPROV_NAMESPACE).read(WFDESC_NAMESPACE).read(WF4EVER_NAMESPACE).read(DCTerms.NS);

	public static final Resource roResource = model.createResource("http://purl.org/wf4ever/ro#Resource");

	public static final Resource aggregatedAnnotation = model
			.createResource("http://purl.org/wf4ever/ro#AggregatedAnnotation");

	public static final Resource foafAgent = model.createResource("http://xmlns.com/foaf/0.1/Agent");

	public static final OntProperty foafName = model.createOntProperty("http://xmlns.com/foaf/0.1/name");

	public static final OntProperty foafPrimaryTopic = model
			.createOntProperty("http://xmlns.com/foaf/0.1/primaryTopic");

	public static final OntProperty filesize = model.createOntProperty("http://purl.org/wf4ever/ro#filesize");

	public static final OntProperty aggregates = model.createOntProperty(Vocab.ORE_NAMESPACE + "aggregates");

	public static final OntProperty annotatesAggregatedResource = model.createOntProperty(Vocab.RO_NAMESPACE
			+ "annotatesAggregatedResource");

	public static final OntProperty aoBody = model.createOntProperty(Vocab.AO_NAMESPACE + "body");

	public static final OntProperty hasSubProcess = model.createOntProperty(Vocab.WFDESC_NAMESPACE + "hasSubProcess");
}