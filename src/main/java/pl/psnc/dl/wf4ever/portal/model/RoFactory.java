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
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Application;
import org.apache.wicket.request.UrlDecoder;
import org.apache.wicket.util.crypt.Base64;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource.Type;
import pl.psnc.dl.wf4ever.portal.services.MyQueryFactory;
import pl.psnc.dl.wf4ever.portal.services.StabilityService;

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
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.vocabulary.DCTerms;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

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

	public static final URI[] defaultRelations = { URI.create(DCTerms.source.getURI()),
			URI.create(DCTerms.references.getURI()), URI.create(DCTerms.isReferencedBy.getURI()),
			URI.create(DCTerms.hasVersion.getURI()), URI.create(DCTerms.isVersionOf.getURI()),
			URI.create(DCTerms.isReplacedBy.getURI()), URI.create(DCTerms.replaces.getURI()),
			URI.create(DCTerms.isFormatOf.getURI()), URI.create(DCTerms.hasFormat.getURI()),
			URI.create(DCTerms.isRequiredBy.getURI()), URI.create(DCTerms.requires.getURI()),
			URI.create(DCTerms.hasPart.getURI()), URI.create(DCTerms.isPartOf.getURI()),
			URI.create("http://purl.org/wf4ever/wfprov#describedByWorkflow"),
			URI.create("http://purl.org/wf4ever/wfprov#wasOutputFrom"),
			URI.create("http://purl.org/wf4ever/wfprov#usedInput")};

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
			Map<URI, Creator> usernames)
		throws URISyntaxException
	{
		OntModel model = createManifestModel(researchObjectURI);
		return createResearchObject(model, researchObjectURI, includeAnnotations, usernames);
	}


	public static ResearchObject createResearchObject(OntModel model, URI researchObjectURI,
			boolean includeAnnotations, Map<URI, Creator> usernames)
		throws URISyntaxException
	{
		return (ResearchObject) createResource(model, researchObjectURI, researchObjectURI, includeAnnotations,
			usernames);
	}


	public static Map<URI, AggregatedResource> getAggregatedResources(OntModel model, URI researchObjectURI,
			Map<URI, Creator> usernames)
		throws URISyntaxException
	{
		Map<URI, AggregatedResource> resources = new HashMap<URI, AggregatedResource>();
		ResearchObject researchObject = createResearchObject(model, researchObjectURI, true, usernames);
		resources.put(researchObjectURI, researchObject);
		Individual ro = model.getIndividual(researchObjectURI.toString());
		NodeIterator it = model.listObjectsOfProperty(ro, aggregates);
		while (it.hasNext()) {
			Individual res = it.next().as(Individual.class);
			if (res.hasRDFType(roResource)) {
				AggregatedResource resource = createResource(model, researchObjectURI, new URI(res.getURI()), true,
					usernames);
				resources.put(resource.getURI(), resource);
			}
		}
		return resources;
	}


	public static void assignResourceGroupsToResources(OntModel model, URI researchObjectURI,
			Set<ResourceGroup> resourceGroups, Map<URI, AggregatedResource> resources)
	{
		ResearchObject ro = (ResearchObject) resources.get(researchObjectURI);

		// TODO take care of proxies
		for (AggregatedResource resource : resources.values()) {
			if (resource.equals(ro)) {
				continue;
			}
			Individual res = model.getIndividual(resource.getURI().toString());
			for (ResourceGroup resourceGroup : resourceGroups) {
				for (URI classURI : resourceGroup.getRdfClasses()) {
					if (res.hasRDFType(classURI.toString())) {
						resource.getMatchingGroups().add(resourceGroup);
						break;
					}
				}
			}
		}
	}


	public static RoTreeModel createConceptualResourcesTree(OntModel model, URI researchObjectURI,
			Map<URI, AggregatedResource> resources)
		throws URISyntaxException
	{
		ResearchObject ro = (ResearchObject) resources.get(researchObjectURI);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(ro);
		RoTreeModel treeModel = new RoTreeModel(rootNode);

		// TODO take care of proxies
		for (AggregatedResource resource : resources.values()) {
			if (resource.equals(ro)) {
				continue;
			}
			treeModel.addAggregatedResource(resource, true);
		}
		return treeModel;
	}


	public static RoTreeModel createPhysicalResourcesTree(OntModel model, URI researchObjectURI,
			Map<URI, AggregatedResource> resources, Map<URI, Creator> usernames)
		throws URISyntaxException
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(resources.get(researchObjectURI));
		RoTreeModel treeModel = new RoTreeModel(rootNode);

		// TODO take care of proxies & folders
		for (AggregatedResource resource : resources.values()) {
			if (isResourceInternal(researchObjectURI, resource.getURI())) {
				treeModel.addAggregatedResource(resource, false);
			}
		}
		return treeModel;
	}


	public static String createRoJSON(Map<URI, AggregatedResource> resources, String[] colors)
		throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JsonFactory jsonFactory = new JsonFactory();
		JsonGenerator jg = jsonFactory.createJsonGenerator(out);
		jg.writeStartArray();
		for (AggregatedResource resource : resources.values()) {
			int rdfClassHashCode = 0;
			for (ResourceGroup g : resource.getMatchingGroups()) {
				rdfClassHashCode += g.hashCode();
			}
			String color = colors[Math.abs(rdfClassHashCode) % colors.length];
			String tooltip = resource instanceof ResearchObject ? "Research Object" : StringUtils.join(
				resource.getMatchingGroups(), ", ");

			jg.writeStartObject();
			jg.writeStringField("id", Base64.encodeBase64URLSafeString(resource.getURI().toString().getBytes()));
			jg.writeStringField("name", resource.getName());
			jg.writeObjectFieldStart("data");
			jg.writeStringField("$color", color);
			jg.writeStringField("tooltip", tooltip);
			jg.writeEndObject();
			jg.writeArrayFieldStart("adjacencies");
			for (AggregatedResource adjacency : resource.getRelations().values()) {
				jg.writeString(Base64.encodeBase64URLSafeString(adjacency.getURI().toString().getBytes()));
			}
			jg.writeEndArray();
			jg.writeEndObject();
		}
		jg.writeEndArray();
		jg.close(); // important: will force flushing of output, close underlying output stream
		return out.toString();
	}


	public static AggregatedResource createResource(URI researchObjectURI, URI resourceURI, boolean includeAnnotations,
			Map<URI, Creator> usernames)
	{
		OntModel model = createManifestModel(researchObjectURI);

		return createResource(model, researchObjectURI, resourceURI, includeAnnotations, usernames);
	}


	/**
	 * @param rootNode
	 * @param resourceURI
	 */
	public static AggregatedResource createResource(OntModel model, URI researchObjectURI, URI resourceURI,
			boolean includeAnnotations, Map<URI, Creator> usernames)
	{

		Individual res = model.getIndividual(resourceURI.toString());
		Calendar created = null;
		List<Creator> creators = new ArrayList<>();
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
				creators.add(getCreator(usernames, node));
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
		//FIXME this looks like a hack
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
	public static Creator getCreator(final Map<URI, Creator> usernames, RDFNode creator)
	{
		if (creator.isURIResource()) {
			final URI uri = URI.create(creator.asResource().getURI());
			try {
				// 1. already fetched
				if (usernames.containsKey(uri)) {
				}
				// 2. FOAF data defined inline
				else if (creator.asResource().hasProperty(foafName)) {
					usernames.put(uri, new Creator(creator.as(Individual.class).getPropertyValue(foafName).asLiteral()
							.getString()));
				}
				else {
					//3. load in a separate thread
					usernames.put(uri, new Creator(uri));
				}
			}
			catch (Exception e) {
				log.error("Error when getting username", e);
				usernames.put(uri, new Creator(uri.toString()));
			}
			return usernames.get(uri);
		}
		else if (creator.isResource()) {
			return new Creator(creator.asResource().getId().toString());
		}
		else {
			return new Creator(creator.asLiteral().getString());
		}
	}


	/**
	 * @param usernames
	 * @param username
	 * @param creator
	 * @return
	 */
	public static Creator getCreator(final Map<URI, Creator> usernames, final String nameOrUri)
	{
		try {
			URI uri = new URI(nameOrUri);
			if (!usernames.containsKey(uri)) {
				//2. load in a separate thread
				usernames.put(uri, new Creator(uri));
			}
			return usernames.get(uri);
		}
		catch (URISyntaxException e) {
			return new Creator(nameOrUri);
		}
	}


	public static void createStabilities(OntModel model, URI researchObjectURI, Map<URI, AggregatedResource> resources)
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


	public static void createRelations(OntModel model, URI researchObjectURI, Map<URI, AggregatedResource> resources)
	{
		for (AggregatedResource resourceAR : resources.values()) {
			Individual resource = model.getIndividual(resourceAR.getURI().toString());
			StmtIterator it = model.listStatements(resource, null, (Resource) null);
			while (it.hasNext()) {
				com.hp.hpl.jena.rdf.model.Statement statement = it.next();
				if (statement.getObject().isURIResource()) {
					URI objectURI = URI.create(statement.getObject().asResource().getURI());
					if (resources.containsKey(objectURI)) {
						AggregatedResource objectAR = resources.get(objectURI);
						Property property = statement.getPredicate();
						resourceAR.getRelations().put(RoFactory.splitCamelCase(property.getLocalName()).toLowerCase(),
							objectAR);
					}
				}
			}
		}
	}


	public static List<Annotation> createAnnotations(URI researchObjectURI, URI resourceURI, Map<URI, Creator> usernames)
	{
		OntModel model = createManifestModel(researchObjectURI);

		return createAnnotations(model, researchObjectURI, resourceURI, usernames);
	}


	public static List<Annotation> createAnnotations(OntModel model, URI researchObjectURI, URI resourceURI,
			Map<URI, Creator> usernames)
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
				Creator creator = getCreator(usernames, ann.getPropertyResourceValue(DCTerms.creator));
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
		URI manifestURI = researchObjectURI.resolve(".ro/manifest.rdf");
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		try {
			model.read(manifestURI.toString());
		}
		catch (DoesNotExistException e) {
			// do nothing, model will be empty
		}
		if (model.isEmpty()) {
			// HACK for old ROs
			manifestURI = researchObjectURI.resolve(".ro/manifest");
			model.read(manifestURI.toString());
		}
		return model;
	}


	/**
	 * @param researchObjectURI
	 * @return
	 */
	public static OntModel createManifestAndAnnotationsModel(URI researchObjectURI)
	{
		URI manifestURI = researchObjectURI.resolve(".ro/manifest.trig");
		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read(manifestURI.toString() + "?original=manifest.rdf", "TRIG");
		if (graphset.countQuads() == 0) {
			// HACK for old ROs
			graphset.read(manifestURI.toString() + "?original=manifest", "TRIG");
		}
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM,
			graphset.asJenaModel(researchObjectURI.resolve(".ro/manifest.rdf").toString()));
		return model;
	}


	/*
	 * from
	 * http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human
	 * -readable-names-in-java
	 */
	public static String splitCamelCase(String s)
	{
		return s.replaceAll(
			String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"),
			" ");
	}


	public static boolean isResourceInternal(URI roURI, URI resourceURI)
	{
		return resourceURI != null && resourceURI.normalize().toString().startsWith(roURI.normalize().toString());
	}
}
