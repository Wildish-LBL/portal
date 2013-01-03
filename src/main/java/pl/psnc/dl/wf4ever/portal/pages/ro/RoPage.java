package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.TreeModel;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.UrlEncoder;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.ROSRSException;
import org.purl.wf4ever.rosrs.client.ROSRService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Annotation;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.model.RoTreeModel;
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.pages.ErrorPage;
import pl.psnc.dl.wf4ever.portal.pages.TemplatePage;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.services.OAuthException;
import pl.psnc.dl.wf4ever.portal.services.RoFactory;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;
import pl.psnc.dl.wf4ever.vocabulary.ORE;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.client.ClientResponse;

/**
 * The Research Object page.
 * 
 * @author piotrekhol
 * 
 */
public class RoPage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(RoPage.class);

    /** RO URI. */
    URI roURI;

    /** Resources aggregated by the RO. */
    Map<URI, AggregatedResource> resources;

    /** Can the user edit the RO. */
    boolean canEdit = false;

    /** The part showing the RO structure. */
    final RoViewerBox roViewerBox;

    /** The part showing the RO annotations. */
    final AnnotatingBox annotatingBox;

    /** The conceptual model of RO resource. */
    private RoTreeModel conceptualResourcesTree;

    /** The physical model of RO resource. */
    private RoTreeModel physicalResourcesTree;

    /** The modal window for editing annotations. */
    final StatementEditModal stmtEditForm;

    /** The modal window for editing relations. */
    final RelationEditModal relEditForm;

    /** The modal window for importing annotations. */
    private final ImportAnnotationModal importAnnotationModal;

    /** The modal window for adding resources. */
    private UploadResourceModal uploadResourceModal;

    /** The feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** Regex pattern for parsing Link HTTP headers. */
    private static final Pattern LINK_HEADER = Pattern.compile("<(.+)>; rel=(.+)");

    /** Template for HTML Link Headers. */
    private static final String HTML_LINK_TEMPLATE = "<link rel=\"%s\" href=\"%s\"/>";


    /**
     * Constructor.
     * 
     * @param parameters
     *            page parameters
     * @throws URISyntaxException
     *             if URIs returned by the RODL are incorrect
     * @throws OAuthException
     *             if it cannot connect to RODL
     * @throws IOException
     *             if it cannot connect to RODL
     */
    @SuppressWarnings("serial")
    public RoPage(final PageParameters parameters)
            throws URISyntaxException, OAuthException, IOException {
        super(parameters);
        if (!parameters.get("ro").isEmpty()) {
            roURI = new URI(parameters.get("ro").toString());
        } else {
            throw new RestartResponseException(ErrorPage.class, new PageParameters().add("message",
                "The RO URI is missing"));
        }

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        if (MySession.get().isSignedIn()) {
            //            List<URI> uris = ROSRService.getROList(rodlURI, MySession.get().getdLibraAccessToken());
            //            canEdit = uris.contains(roURI);
            canEdit = true;
        }
        add(new Label("title", roURI.toString()));

        final CompoundPropertyModel<AggregatedResource> itemModel = new CompoundPropertyModel<AggregatedResource>(
                (AggregatedResource) null);
        roViewerBox = new RoViewerBox(this, itemModel, new PropertyModel<TreeModel>(this, "conceptualResourcesTree"),
                new PropertyModel<TreeModel>(this, "physicalResourcesTree"), "loadingROFragment");
        add(roViewerBox);
        annotatingBox = new AnnotatingBox(this, itemModel);
        add(annotatingBox);
        annotatingBox.selectedStatements.clear();
        add(new DownloadMetadataModal("downloadMetadataModal", this));
        uploadResourceModal = new UploadResourceModal("uploadResourceModal", this,
                ((PortalApplication) getApplication()).getResourceGroups());
        add(uploadResourceModal);
        stmtEditForm = new StatementEditModal("statementEditModal", RoPage.this, new CompoundPropertyModel<Statement>(
                (Statement) null));
        add(stmtEditForm);
        relEditForm = new RelationEditModal("relationEditModal", RoPage.this, new CompoundPropertyModel<Statement>(
                (Statement) null), "loadingROFragment");
        add(relEditForm);
        importAnnotationModal = new ImportAnnotationModal("importAnnotationModal", this, itemModel);
        add(importAnnotationModal);

        add(new RoEvoBox("roEvoBox", ((PortalApplication) getApplication()).getSparqlEndpointURI(), roURI));

        add(new AbstractDefaultAjaxBehavior() {

            @Override
            protected void respond(AjaxRequestTarget target) {
                try {
                    if (getConceptualResourcesTree() == null || getPhysicalResourcesTree() == null) {
                        PortalApplication app = ((PortalApplication) getApplication());
                        Map<URI, Creator> usernames = MySession.get().getUsernames();
                        OntModel model = ROSRService.createManifestAndAnnotationsModel(roURI);
                        resources = RoFactory.getAggregatedResources(model, rodlURI, roURI, usernames);
                        RoFactory.assignResourceGroupsToResources(model, roURI, app.getResourceGroups(), resources);
                        setConceptualResourcesTree(RoFactory.createConceptualResourcesTree(roURI, resources));
                        setPhysicalResourcesTree(RoFactory.createPhysicalResourcesTree(roURI, resources));

                        RoFactory.createRelations(model, roURI, resources);
                        // FIXME this has been turned off because it takes too much time and is generally a hack
                        //						RoFactory.createStabilities(model, roURI, resources);

                        roViewerBox.onRoTreeLoaded();
                        relEditForm.onRoTreeLoaded();
                        target.add(roViewerBox);
                    }
                } catch (URISyntaxException e) {
                    LOG.error(e);
                    error(e);
                }
                target.add(feedbackPanel);
            }


            @Override
            public void renderHead(final Component component, final IHeaderResponse response) {
                super.renderHead(component, response);
                response.renderOnDomReadyJavaScript(getCallbackScript().toString());
            }

        });
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try {
            ClientResponse head = MySession.get().getRosrs().getResourceHead(roURI.resolve(".ro/manifest.rdf"));
            List<String> links = head.getHeaders().get("Link");
            if (links != null) {
                for (String link : links) {
                    Matcher m = LINK_HEADER.matcher(link);
                    if (m.matches()) {
                        response.renderString(String.format(HTML_LINK_TEMPLATE, m.group(2), m.group(1)));
                    }
                }
            }
            head.close();
        } catch (ROSRSException e) {
            LOG.error("Unexpected response when getting RO head", e);
        }
    }


    public RoTreeModel getConceptualResourcesTree() {
        return conceptualResourcesTree;
    }


    public void setConceptualResourcesTree(RoTreeModel conceptualResourcesTree) {
        this.conceptualResourcesTree = conceptualResourcesTree;
    }


    public RoTreeModel getPhysicalResourcesTree() {
        return physicalResourcesTree;
    }


    public void setPhysicalResourcesTree(RoTreeModel physicalResourcesTree) {
        this.physicalResourcesTree = physicalResourcesTree;
    }


    /**
     * A utility class for creating an external link to a property of a statement.
     * 
     * @author piotrekhol
     * 
     */
    @SuppressWarnings("serial")
    class ExternalLinkFragment extends Fragment {

        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param markupId
         *            fragment wicket id
         * @param markupProvider
         *            which component defines the fragment
         * @param model
         *            statement model
         * @param property
         *            property of a statement
         */
        public ExternalLinkFragment(String id, String markupId, MarkupContainer markupProvider,
                CompoundPropertyModel<Statement> model, String property) {
            super(id, markupId, markupProvider, model);
            add(new ExternalLink("link", model.<String> bind(property), model.<String> bind(property)));
        }
    }


    /**
     * A utility class for creating links to resources inside the RO.
     * 
     * @author piotrekhol
     * 
     */
    @SuppressWarnings("serial")
    class InternalLinkFragment extends Fragment {

        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param markupId
         *            fragment wicket id
         * @param markupProvider
         *            which component defines the fragment
         * @param statement
         *            the statement for which the link is created
         */
        public InternalLinkFragment(String id, String markupId, MarkupContainer markupProvider, Statement statement) {
            super(id, markupId, markupProvider);
            String internalName = "./" + roURI.relativize(statement.getSubjectURI()).toString();
            add(new AjaxLink<String>("link", new Model<String>(internalName)) {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    // TODO Auto-generated method stub

                }
            }.add(new Label("name", internalName.toString())));
        }
    }


    /**
     * A utility class for creating a link for editing a statement.
     * 
     * @author piotrekhol
     * 
     */
    @SuppressWarnings("serial")
    class EditLinkFragment extends Fragment {

        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param markupId
         *            fragment wicket id
         * @param markupProvider
         *            which component defines the fragment
         * @param link
         *            link defining the action upon click
         */
        public EditLinkFragment(String id, String markupId, MarkupContainer markupProvider,
                AjaxFallbackLink<String> link) {
            super(id, markupId, markupProvider);
            add(link);
        }
    }


    /**
     * Called when the user wants to add a local resource.
     * 
     * @param target
     *            response target
     * @param uploadedFile
     *            the uploaded file
     * @param selectedResourceGroups
     *            resource groups of the file
     * @throws ROSRSException
     *             unexpected response code
     * @throws IOException
     *             can't get the uploaded file
     */
    void onResourceAdd(AjaxRequestTarget target, final FileUpload uploadedFile,
            Set<ResourceGroup> selectedResourceGroups)
            throws ROSRSException, IOException {
        URI resourceURI = roURI.resolve(UrlEncoder.PATH_INSTANCE.encode(uploadedFile.getClientFileName(), "UTF-8"));
        MySession
                .get()
                .getRosrs()
                .aggregateInternalResource(roURI, uploadedFile.getClientFileName(), uploadedFile.getInputStream(),
                    uploadedFile.getContentType());
        //        OntModel manifestModel = ROSRService.createManifestModel(roURI);
        //        Individual individual = manifestModel.createResource(resourceURI.toString()).as(Individual.class);
        //        for (ResourceGroup resourceGroup : selectedResourceGroups) {
        //            individual.addRDFType(manifestModel.createResource(resourceGroup.getRdfClasses().iterator().next()
        //                    .toString()));
        //        }
        //        response = ROSRService.uploadManifestModel(roURI, manifestModel, MySession.get().getdLibraAccessToken());
        //        if (response.getStatus() != HttpServletResponse.SC_OK) {
        //            throw new IOException(response.getClientResponseStatus().getReasonPhrase());
        //        }

        AggregatedResource resource = RoFactory.createResource(rodlURI, roURI, resourceURI, true, MySession.get()
                .getUsernames());
        resource.getMatchingGroups().addAll(selectedResourceGroups);
        getConceptualResourcesTree().addAggregatedResource(resource, true);
        getPhysicalResourcesTree().addAggregatedResource(resource, false);
        target.add(roViewerBox);

        resources.put(resourceURI, resource);
        RoFactory.addRelation(resources.get(roURI), ORE.aggregates, resource);

        roViewerBox.renderJSComponents(resources, target);
    }


    /**
     * Called when a user wants to a delete an aggregated resource.
     * 
     * @param resource
     *            remote or local resource
     * @param target
     *            request target
     * @throws IOException
     *             when cannot connect to RODL
     * @throws ROSRSException
     *             deleting the resource caused an unexpected response
     */
    public void onResourceDelete(AggregatedResource resource, AjaxRequestTarget target)
            throws IOException, ROSRSException {
        ClientResponse response = MySession.get().getRosrs().deleteResource(resource.getURI());
        if (response.getStatus() != HttpServletResponse.SC_NO_CONTENT) {
            onRemoteResourceDelete(resource, target);
        }
        getConceptualResourcesTree().removeAggregatedResource(resource);
        getPhysicalResourcesTree().removeAggregatedResource(resource);

        resources.remove(resource.getURI());
        for (Entry<String, AggregatedResource> entry : resource.getInverseRelations().entries()) {
            entry.getValue().getRelations().remove(entry.getKey(), resource);
        }
        roViewerBox.renderJSComponents(resources, target);
        target.add(roViewerBox);
    }


    /**
     * Called when a user wants to delete a remote resource.
     * 
     * @param resource
     *            a remote resource
     * @param target
     *            request target
     * @throws IOException
     *             can't connect to RODL
     * @throws ROSRSException
     *             unexpected response code when deleting the resource
     */
    private void onRemoteResourceDelete(AggregatedResource resource, AjaxRequestTarget target)
            throws IOException, ROSRSException {
        OntModel manifestModel = ROSRService.createManifestModel(roURI);
        URI absoluteResourceURI = roURI.resolve(resource.getURI());
        Resource individual = manifestModel.createResource(absoluteResourceURI.toString());
        ResIterator it = manifestModel.listSubjectsWithProperty(ORE.proxyFor, individual);
        if (!it.hasNext()) {
            throw new IOException("Not found");
        }
        Resource proxy = it.next();
        MySession.get().getRosrs().deleteResource(URI.create(proxy.getURI()));
    }


    /**
     * Called when the currently selected aggregated resource has changed.
     * 
     * @param target
     *            request target
     */
    public void onResourceSelected(AjaxRequestTarget target) {
        annotatingBox.selectedStatements.clear();
        target.add(annotatingBox);
    }


    /**
     * Called when a remote resource is added.
     * 
     * @param target
     *            request target
     * @param resourceURI
     *            remote resource URI
     * @param downloadURI
     *            remote resource downloadURI (unused)
     * @param selectedTypes
     *            resource groups that this resource belongs to
     * @throws IOException
     *             Jackson error
     * @throws ROSRSException
     *             aggregating the resource caused unexpected response
     */
    public void onRemoteResourceAdded(AjaxRequestTarget target, URI resourceURI, URI downloadURI,
            Set<ResourceGroup> selectedTypes)
            throws IOException, ROSRSException {
        URI absoluteResourceURI = roURI.resolve(resourceURI);
        MySession.get().getRosrs().aggregateExternalResource(roURI, resourceURI);

        AggregatedResource resource = RoFactory.createResource(rodlURI, roURI, absoluteResourceURI, true, MySession
                .get().getUsernames());
        resource.getMatchingGroups().addAll(selectedTypes);
        getConceptualResourcesTree().addAggregatedResource(resource, true);
        getPhysicalResourcesTree().addAggregatedResource(resource, false);
        target.add(roViewerBox);

        resources.put(absoluteResourceURI, resource);
        RoFactory.addRelation(resources.get(roURI), ORE.aggregates, resource);

        roViewerBox.renderJSComponents(resources, target);
    }


    /**
     * Called when the user wants to create a new annotation.
     * 
     * @param statements
     *            a list of statements to be put in the annotation
     * @throws URISyntaxException
     *             URIs retrieved from RODL are incorrect
     * @throws ROSRSException
     *             requests to RODL returned incorrect responses
     */
    void onStatementAdd(List<Statement> statements)
            throws URISyntaxException, ROSRSException {
        InputStream in = RoFactory.wrapAnnotationBody(statements);
        MySession
                .get()
                .getRosrs()
                .addAnnotation(roURI, new HashSet<>(Arrays.asList(statements.get(0).getSubjectURI())),
                    ".ro/" + UUID.randomUUID().toString(), in, "application/rdf+xml");
    }


    /**
     * User has edited a statement of an annotation.
     * 
     * @param statement
     *            the statement
     * @throws ROSRSException
     *             requests to RODL returned incorrect responses
     */
    void onStatementEdit(Statement statement)
            throws ROSRSException {
        Annotation ann = statement.getAnnotation();
        MySession.get().getRosrs()
                .updateResource(ann.getBodyURI(), RoFactory.wrapAnnotationBody(ann.getBody()), "application/rdf+xml");
    }


    /**
     * Called after an annotation has been created or edited.
     * 
     * @param target
     *            request target.
     */
    void onStatementAddedEdited(AjaxRequestTarget target) {
        //FIXME isn't it the same as the next one?
        this.annotatingBox.getModelObject().setAnnotations(
            RoFactory.createAnnotations(rodlURI, roURI, this.annotatingBox.getModelObject().getURI(), MySession.get()
                    .getUsernames()));
        target.add(roViewerBox.infoPanel);
        target.add(annotatingBox);
    }


    /**
     * Called when a relation is added or edited.
     * 
     * @param statement
     *            the relation
     * @param target
     *            request target
     */
    void onRelationAddedEdited(Statement statement, AjaxRequestTarget target) {
        this.annotatingBox.getModelObject().setAnnotations(
            RoFactory.createAnnotations(rodlURI, roURI, this.annotatingBox.getModelObject().getURI(), MySession.get()
                    .getUsernames()));
        AggregatedResource subjectAR = resources.get(statement.getSubjectURI());
        AggregatedResource objectAR = resources.get(statement.getObjectURI());
        RoFactory.addRelation(subjectAR, statement.getPropertyURI(), objectAR);
        target.add(roViewerBox.infoPanel);
        target.add(annotatingBox);
    }


    /**
     * Called when an annotation body has been uploaded.
     * 
     * @param target
     *            request target
     * @param uploadedFile
     *            uploaded file
     * @param aggregatedResource
     *            resource annotated
     * @throws IOException
     *             can't get the uploaded file
     * @throws URISyntaxException
     *             problems with creating the annotation
     * @throws ROSRSException
     *             uploading the annotation body returned an unexpected response
     */
    public void onAnnotationImport(AjaxRequestTarget target, FileUpload uploadedFile,
            AggregatedResource aggregatedResource)
            throws IOException, URISyntaxException, ROSRSException {
        String contentType = RDFFormat.forFileName(uploadedFile.getClientFileName(), RDFFormat.RDFXML)
                .getDefaultMIMEType();
        MySession
                .get()
                .getRosrs()
                .addAnnotation(roURI, new HashSet<>(Arrays.asList(aggregatedResource.getURI())),
                    uploadedFile.getClientFileName(), uploadedFile.getInputStream(), contentType);
        setResponsePage(RoPage.class, getPageParameters());
    }


    public MyFeedbackPanel getFeedbackPanel() {
        return feedbackPanel;
    }


    public String getROZipLink() {
        return roURI.toString().replaceFirst("/ROs/", "/zippedROs/");
    }


    /**
     * Return a link to a format-specific version of the manifest.
     * 
     * @param format
     *            RDF format
     * @return a URI as string of the resource
     */
    public String getROMetadataLink(RDFFormat format) {
        return roURI.resolve(".ro/manifest." + format.getDefaultFileExtension() + "?original=manifest.rdf").toString();
    }
}
