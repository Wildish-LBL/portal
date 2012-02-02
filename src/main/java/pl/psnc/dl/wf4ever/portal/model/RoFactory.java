/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;
import org.apache.wicket.request.UrlDecoder;

import com.google.common.collect.Multimap;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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

	private static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	private static final String AO_NAMESPACE = "http://purl.org/ao/";

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

	public static final Property annotatesResource = ModelFactory.createDefaultModel().createProperty(
		AO_NAMESPACE + "annotatesResource");

	public static final Property aoBody = ModelFactory.createDefaultModel().createProperty(AO_NAMESPACE + "body");


	public static ResearchObject createResearchObject(URI researchObjectURI, boolean includeAnnotations)
		throws URISyntaxException
	{
		OntModel model = createManifestModel(researchObjectURI);
		return createResearchObject(model, researchObjectURI, includeAnnotations);
	}


	public static ResearchObject createResearchObject(OntModel model, URI researchObjectURI, boolean includeAnnotations)
		throws URISyntaxException
	{
		Calendar created = null;
		String creator = null;
		Individual ro = model.getIndividual(researchObjectURI.toString());
		try {
			created = ((XSDDateTime) ro.getPropertyValue(DCTerms.created).asLiteral().getValue()).asCalendar();
		}
		catch (Exception e) {
			log.warn("RO " + researchObjectURI + " does not define created");
		}
		try {
			creator = ro.getPropertyResourceValue(DCTerms.creator).as(Individual.class).getPropertyValue(foafName)
					.asLiteral().getString();
		}
		catch (Exception e) {
			log.warn("RO " + researchObjectURI + " does not define a creator");
		}
		ResearchObject researchObject = new ResearchObject(researchObjectURI, created, creator);
		if (includeAnnotations) {
			researchObject.setAnnotations(createAnnotations(model, researchObjectURI, researchObjectURI));
		}
		return researchObject;
	}


	public static TreeModel createAggregatedResourcesTree(URI researchObjectURI, Multimap<String, URI> resourceGroups)
		throws URISyntaxException
	{
		OntModel model = createManifestAndAnnotationsModel(researchObjectURI);
		return createAggregatedResourcesTree(model, researchObjectURI, resourceGroups);
	}


	public static TreeModel createAggregatedResourcesTree(OntModel model, URI researchObjectURI,
			Multimap<String, URI> resourceGroups)
		throws URISyntaxException
	{
		ResearchObject researchObject = createResearchObject(researchObjectURI, true);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(researchObject);

		Map<String, DefaultMutableTreeNode> groupNodes = new HashMap<>();

		// TODO take care of proxies & folders
		Individual ro = model.getIndividual(researchObjectURI.toString());
		NodeIterator it = model.listObjectsOfProperty(ro, aggregates);
		while (it.hasNext()) {
			Individual res = it.next().as(Individual.class);
			if (res.hasRDFType(roResource)) {
				AggregatedResource resource = createResource(model, researchObjectURI, new URI(res.getURI()), true);
				boolean foundGroup = false;
				for (String group : resourceGroups.keySet()) {
					for (URI classURI : resourceGroups.get(group)) {
						if (res.hasRDFType(classURI.toString())) {
							foundGroup = true;
							if (!groupNodes.containsKey(group)) {
								groupNodes.put(group, new DefaultMutableTreeNode(group));
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
		return new DefaultTreeModel(rootNode);
	}


	public static AggregatedResource createResource(URI researchObjectURI, URI resourceURI, boolean includeAnnotations)
	{
		OntModel model = createManifestModel(researchObjectURI);

		return createResource(model, researchObjectURI, resourceURI, includeAnnotations);
	}


	/**
	 * @param rootNode
	 * @param resourceURI
	 */
	public static AggregatedResource createResource(OntModel model, URI researchObjectURI, URI resourceURI,
			boolean includeAnnotations)
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
		AggregatedResource resource = new RoResource(resourceURI, created, creator, name, size);
		if (includeAnnotations) {
			resource.setAnnotations(createAnnotations(model, researchObjectURI, resourceURI));
		}
		return resource;
	}


	public static List<Annotation> createAnnotations(URI researchObjectURI, URI resourceURI)
	{
		OntModel model = createManifestModel(researchObjectURI);

		return createAnnotations(model, researchObjectURI, resourceURI);
	}


	public static List<Annotation> createAnnotations(OntModel model, URI researchObjectURI, URI resourceURI)
	{
		List<Annotation> anns = new ArrayList<>();

		Individual res = model.getIndividual(resourceURI.toString());
		ResIterator it = model.listSubjectsWithProperty(annotatesResource, res);
		while (it.hasNext()) {
			Individual ann = it.next().as(Individual.class);
			try {
				Calendar created = ((XSDDateTime) ann.getPropertyValue(DCTerms.created).asLiteral().getValue())
						.asCalendar();
				String creator = ann.getPropertyResourceValue(DCTerms.creator).as(Individual.class)
						.getPropertyValue(foafName).asLiteral().getString();
				Resource body = ann.getPropertyResourceValue(aoBody);
				String name = UrlDecoder.PATH_INSTANCE.decode(researchObjectURI.relativize(new URI(ann.getURI()))
						.toString(), "UTF-8");
				anns.add(new Annotation(new URI(ann.getURI()), created, creator, name, new URI(body.getURI())));
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
	public static OntModel createManifestAndAnnotationsModel(URI researchObjectURI)
	{
		URI manifestURI = researchObjectURI.resolve(".ro/manifest");

		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read(manifestURI.toString() + ".trig", "TRIG");
		OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM,
			graphset.asJenaModel(manifestURI.toString()));
		return ontModel;
	}

}
