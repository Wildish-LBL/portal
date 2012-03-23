/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.request.UrlDecoder;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource.Type;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;
import pl.psnc.dl.wf4ever.portal.services.StabilityService;

import com.google.common.collect.Multimap;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.sparql.NamedGraphDataset;

/**
 * @author piotrhol
 * 
 */
public class RoFactory
{

	private static final Logger log = Logger.getLogger(RoFactory.class);

	private static final String RO_NAMESPACE = "http://purl.org/wf4ever/ro#";

	private static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	private static final String AO_NAMESPACE = "http://purl.org/ao/";

	@SuppressWarnings("unused")
	private static final String WFPROV_NAMESPACE = "http://purl.org/wf4ever/wfprov#";

	private static final String WFDESC_NAMESPACE = "http://purl.org/wf4ever/wfdesc#";

	@SuppressWarnings("unused")
	private static final String WF4EVER_NAMESPACE = "http://purl.org/wf4ever/wf4ever#";

	public static final URI[] defaultProperties = { URI.create(DCTerms.type.getURI()),
			URI.create(DCTerms.subject.getURI()), URI.create(DCTerms.description.getURI()),
			URI.create(DCTerms.format.getURI()), URI.create(DCTerms.title.getURI()),
			URI.create(DCTerms.created.getURI())};

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
		ORE_NAMESPACE + "aggregates");

	public static final Property annotatesAggregatedResource = ModelFactory.createDefaultModel().createProperty(
		RO_NAMESPACE + "annotatesAggregatedResource");

	public static final Property aoBody = ModelFactory.createDefaultModel().createProperty(AO_NAMESPACE + "body");

	public static final Property hasSubProcess = ModelFactory.createDefaultModel().createProperty(
		WFDESC_NAMESPACE + "hasSubProcess");


	public static ResearchObject createResearchObject(URI researchObjectURI, boolean includeAnnotations,
			Map<URI, String> usernames)
		throws URISyntaxException
	{
		OntModel model = createManifestModel(researchObjectURI);
		return createResearchObject(model, researchObjectURI, includeAnnotations, usernames);
	}


	public static ResearchObject createResearchObject(OntModel model, URI researchObjectURI,
			boolean includeAnnotations, Map<URI, String> usernames)
		throws URISyntaxException
	{
		return (ResearchObject) createResource(model, researchObjectURI, researchObjectURI, includeAnnotations,
			usernames);
	}


	public static RoTreeModel createAggregatedResourcesTree(URI researchObjectURI,
			Multimap<String, URI> resourceGroups, Map<String, String> resourceGroupDescriptions,
			Map<URI, String> usernames)
		throws URISyntaxException
	{
		ResearchObject researchObject = createResearchObject(researchObjectURI, true, usernames);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(researchObject);

		Map<String, DefaultMutableTreeNode> groupNodes = new HashMap<>();
		Map<URI, AggregatedResource> resources = new HashMap<URI, AggregatedResource>();
		resources.put(researchObjectURI, researchObject);

		NamedGraphSet graphset = createManifestAndAnnotationsModel(researchObjectURI);
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM,
			graphset.asJenaModel(researchObjectURI.resolve(".ro/manifest").toString()));

		// TODO take care of proxies & folders
		Individual ro = model.getIndividual(researchObjectURI.toString());
		NodeIterator it = model.listObjectsOfProperty(ro, aggregates);
		while (it.hasNext()) {
			Individual res = it.next().as(Individual.class);
			if (res.hasRDFType(roResource)) {
				AggregatedResource resource = createResource(model, researchObjectURI, new URI(res.getURI()), true,
					usernames);
				resources.put(resource.getURI(), resource);
				boolean foundGroup = false;
				for (String group : resourceGroups.keySet()) {
					for (URI classURI : resourceGroups.get(group)) {
						if (res.hasRDFType(classURI.toString())) {
							foundGroup = true;
							if (!groupNodes.containsKey(group)) {
								ResourceGroup resGroup = new ResourceGroup(group, resourceGroupDescriptions.get(group));
								groupNodes.put(group, new DefaultMutableTreeNode(resGroup));
							}
							groupNodes.get(group).add(new DefaultMutableTreeNode(resource));
							break;
						}
					}
				}
				if (!foundGroup) {
					rootNode.add(new DefaultMutableTreeNode(resource));
				}
			}
		}
		int i = 0;
		for (Entry<String, DefaultMutableTreeNode> e : groupNodes.entrySet()) {
			rootNode.insert(e.getValue(), i++);
		}

		createRelations(graphset, model, researchObjectURI, resources);
		createStabilities(model, researchObjectURI, resources);
		return new RoTreeModel(rootNode);
	}


	public static AggregatedResource createResource(URI researchObjectURI, URI resourceURI, boolean includeAnnotations,
			Map<URI, String> usernames)
	{
		OntModel model = createManifestModel(researchObjectURI);

		return createResource(model, researchObjectURI, resourceURI, includeAnnotations, usernames);
	}


	/**
	 * @param rootNode
	 * @param resourceURI
	 */
	public static AggregatedResource createResource(OntModel model, URI researchObjectURI, URI resourceURI,
			boolean includeAnnotations, Map<URI, String> usernames)
	{

		Individual res = model.getIndividual(resourceURI.toString());
		Calendar created = null;
		List<String> creators = new ArrayList<>();
		long size = 0;

		try {
			created = ((XSDDateTime) res.getPropertyValue(DCTerms.created).asLiteral().getValue()).asCalendar();
		}
		catch (Exception e) {
		}
		try {
			NodeIterator it = res.listPropertyValues(DCTerms.creator);
			while (it.hasNext()) {
				RDFNode node = it.next();
				if (node.isResource()) {
					creators.add(getUserName(usernames, node.asResource()));
				}
				else {
					creators.add(node.asLiteral().getString());
				}
			}
		}
		catch (Exception e) {
		}
		try {
			size = res.getPropertyValue(filesize).asLiteral().getLong();
		}
		catch (Exception e) {
		}

		String name = UrlDecoder.PATH_INSTANCE.decode(researchObjectURI.relativize(resourceURI).toString(), "UTF-8");
		AggregatedResource resource;
		if (res.hasRDFType("http://purl.org/wf4ever/ro#ResearchObject")) {
			resource = new ResearchObject(resourceURI, created, creators);
		}
		else if (resourceURI.getPath().endsWith(".t2flow") || res.hasRDFType("http://purl.org/wf4ever/wfdesc#Workflow")) {
			resource = new InternalResource(resourceURI, created, creators, name, size, Type.WORKFLOW);
		}
		else if (res.hasRDFType("http://purl.org/wf4ever/wf4ever#WebServiceProcess")) {
			resource = new WebService(resourceURI, created, creators, name, size);
		}
		else {
			resource = new InternalResource(resourceURI, created, creators, name, size, Type.OTHER);
		}
		if (includeAnnotations) {
			resource.setAnnotations(createAnnotations(model, researchObjectURI, resourceURI, usernames));
		}
		return resource;
	}


	/**
	 * @param usernames
	 * @param username
	 * @param creator
	 * @return
	 */
	private static String getUserName(Map<URI, String> usernames, Resource creator)
	{
		String username = null;
		URI uri = URI.create(creator.getURI());
		try {
			// 1. already fetched
			if (usernames.containsKey(uri)) {
				username = usernames.get(uri);
			}
			// 2. FOAF data defined inline
			else if (creator.hasProperty(foafName)) {
				username = creator.as(Individual.class).getPropertyValue(foafName).asLiteral().getString();
			}
			else {
				try {
					// 3. FOAF data under user URI
					OntModel userModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
					userModel.read(uri.toString(), null);
					Resource r2 = userModel.createResource(uri.toString());
					if (r2 != null && r2.hasProperty(foafName)) {
						username = r2.as(Individual.class).getPropertyValue(foafName).asLiteral().getString();
					}
				}
				catch (Exception e) {
					log.debug("No FOAF data under user URI", e);
				}
				if (username == null) {
					// 4. FOAF data in RODL
					OntModel userModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
					userModel.read(ROSRService.getUser(uri), null);
					Resource r2 = userModel.createResource(uri.toString());
					if (r2 != null && r2.hasProperty(foafName)) {
						username = r2.as(Individual.class).getPropertyValue(foafName).asLiteral().getString();
					}
				}
			}
		}
		catch (Exception e) {
			log.error("Error when getting username", e);
		}
		if (username == null) {
			username = uri.toString();
		}
		usernames.put(uri, username);
		return username;
	}


	private static void createStabilities(OntModel model, URI researchObjectURI, Map<URI, AggregatedResource> resources)
	{
		PortalApplication app = (PortalApplication) Application.get();
		try {
			QueryExecution qexec = QueryExecutionFactory.create(
				MyQueryFactory.getProvenanceTraces(researchObjectURI.toString()), model);
			ResultSet result = qexec.execSelect();
			while (result.hasNext()) {
				QuerySolution solution = result.next();
				Resource resource = solution.get("resource").asResource();
				Resource trace = solution.get("trace").asResource();
				if (resource.isURIResource() && trace.isURIResource()) {
					AggregatedResource resourceAR = resources.get(URI.create(resource.getURI()));
					if (resourceAR != null) {
						try {
							double score = StabilityService.calculateStability(app.getStabilityEndpointURL().toURI(),
								URI.create(trace.getURI()));
							resourceAR.setStability(score);
							resourceAR.setProvenanceTraceURI(URI.create(trace.getURI()));
						}
						catch (Exception e) {
							log.error(e);
							resourceAR.setStability(-1);
						}
					}
				}
			}
			qexec.close();
		}
		catch (IOException e) {
			log.error(e.getMessage());
		}
	}


	// FIXME this is too specific, a more universal method would be better
	private static void createRelations(NamedGraphSet graphset, OntModel model, URI researchObjectURI,
			Map<URI, AggregatedResource> resources)
	{
		for (AggregatedResource resourceAR : resources.values()) {
			Individual resource = model.getIndividual(resourceAR.getURI().toString());
			StmtIterator it = model.listStatements(resource, null, (Resource) null);
			while (it.hasNext()) {
				com.hp.hpl.jena.rdf.model.Statement statement = it.next();
				if (statement.getObject().isURIResource()) {
					URI objectURI = URI.create(statement.getObject().asResource().getURI());
					if (resources.containsKey(objectURI)) {
						Property property = statement.getPredicate();
						AggregatedResource objectAR = resources.get(objectURI);
						if (property.equals(DCTerms.source)) {
							resourceAR.getRelations().put("Has sources", objectAR);
							objectAR.getRelations().put("Is the source of", resourceAR);
						}
						if (property.equals(DCTerms.relation)) {
							resourceAR.getRelations().put("Is related to", objectAR);
							objectAR.getRelations().put("Is related to", resourceAR);
						}
						if (property.equals(DCTerms.isReferencedBy)) {
							resourceAR.getRelations().put("Is referenced by", objectAR);
							objectAR.getRelations().put("References", resourceAR);
						}
						if (property.equals(hasSubProcess)) {
							resourceAR.getRelations().put("Has subprocesses", objectAR);
							objectAR.getRelations().put("Is a subprocess of", resourceAR);
						}
					}
				}
			}
		}
		try {
			QueryExecution qexec = QueryExecutionFactory.create(
				MyQueryFactory.getWorkflowOutputs(researchObjectURI.toString()), new NamedGraphDataset(graphset));
			ResultSet result = qexec.execSelect();
			while (result.hasNext()) {
				QuerySolution solution = result.next();
				Resource workflow = solution.get("workflow").asResource();
				Resource output = solution.get("resource").asResource();
				if (workflow.isURIResource() && output.isURIResource()) {
					AggregatedResource workflowAR = resources.get(URI.create(workflow.getURI()));
					AggregatedResource outputAR = resources.get(URI.create(output.getURI()));
					if (workflowAR != null && outputAR != null) {
						workflowAR.getRelations().put("Generated as output", outputAR);
						outputAR.getRelations().put("Is an output of", workflowAR);
					}
				}
			}
			qexec.close();
		}
		catch (IOException e) {
			log.error(e.getMessage());
		}
		try {
			QueryExecution qexec = QueryExecutionFactory.create(
				MyQueryFactory.getWorkflowInputs(researchObjectURI.toString()), new NamedGraphDataset(graphset));
			ResultSet result = qexec.execSelect();
			while (result.hasNext()) {
				QuerySolution solution = result.next();
				Resource workflow = solution.get("workflow").asResource();
				Resource input = solution.get("resource").asResource();
				if (workflow.isURIResource() && input.isURIResource()) {
					AggregatedResource workflowAR = resources.get(URI.create(workflow.getURI()));
					AggregatedResource inputAR = resources.get(URI.create(input.getURI()));
					if (workflowAR != null && inputAR != null) {
						workflowAR.getRelations().put("Used as input", inputAR);
						inputAR.getRelations().put("Is input to", workflowAR);
					}
				}
			}
			qexec.close();
		}
		catch (IOException e) {
			log.error(e.getMessage());
		}
	}


	public static List<Annotation> createAnnotations(URI researchObjectURI, URI resourceURI, Map<URI, String> usernames)
	{
		OntModel model = createManifestModel(researchObjectURI);

		return createAnnotations(model, researchObjectURI, resourceURI, usernames);
	}


	public static List<Annotation> createAnnotations(OntModel model, URI researchObjectURI, URI resourceURI,
			Map<URI, String> usernames)
	{
		List<Annotation> anns = new ArrayList<>();

		Individual res = model.getIndividual(resourceURI.toString());
		ResIterator it = model.listSubjectsWithProperty(annotatesAggregatedResource, res);
		while (it.hasNext()) {
			Individual ann = it.next().as(Individual.class);
			if (!ann.hasRDFType(aggregatedAnnotation))
				continue;
			try {
				Calendar created = ((XSDDateTime) ann.getPropertyValue(DCTerms.created).asLiteral().getValue())
						.asCalendar();
				String creator = getUserName(usernames, ann.getPropertyResourceValue(DCTerms.creator));
				Resource body = ann.getPropertyResourceValue(aoBody);
				String name = UrlDecoder.PATH_INSTANCE.decode(researchObjectURI.relativize(new URI(ann.getURI()))
						.toString(), "UTF-8");
				anns.add(new Annotation(new URI(ann.getURI()), created, Arrays.asList(creator), name, new URI(body
						.getURI())));
			}
			catch (Exception e) {
				log.warn("Could not add annotation " + ann.getURI() + ": " + e.getMessage());
			}
		}
		Collections.sort(anns, new Comparator<Annotation>() {

			@Override
			public int compare(Annotation a1, Annotation a2)
			{
				return a1.getCreated().compareTo(a2.getCreated());
			}
		});
		return anns;
	}


	public static List<Statement> createAnnotationBody(Annotation annotation, URI annotationBodyURI)
		throws URISyntaxException
	{
		Model body = ModelFactory.createDefaultModel();
		try {
			body.read(annotationBodyURI.toString());
		}
		catch (Exception e) {
			return null;
		}

		List<Statement> statements = new ArrayList<Statement>();
		StmtIterator it = body.listStatements();
		while (it.hasNext()) {
			statements.add(new Statement(it.next(), annotation));
		}
		Collections.sort(statements, new Comparator<Statement>() {

			@Override
			public int compare(Statement s1, Statement s2)
			{
				return s1.getPropertyLocalName().compareTo(s2.getPropertyLocalName());
			}
		});
		return statements;
	}


	public static InputStream wrapAnnotationBody(List<Statement> statements)
	{
		Model body = ModelFactory.createDefaultModel();
		for (Statement stmt : statements) {
			body.add(stmt.createJenaStatement());
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		body.write(out);
		return new ByteArrayInputStream(out.toByteArray());
	}


	/**
	 * @param researchObjectURI
	 * @return
	 */
	public static OntModel createManifestModel(URI researchObjectURI)
	{
		URI manifestURI = researchObjectURI.resolve(".ro/manifest");
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		model.read(manifestURI.toString());
		return model;
	}


	/**
	 * @param researchObjectURI
	 * @return
	 */
	public static NamedGraphSet createManifestAndAnnotationsModel(URI researchObjectURI)
	{
		URI manifestURI = researchObjectURI.resolve(".ro/manifest");

		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read(manifestURI.toString() + ".trig", "TRIG");
		return graphset;
	}

}
