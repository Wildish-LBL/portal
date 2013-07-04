package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.purl.wf4ever.checklist.client.ChecklistEvaluationService;
import org.purl.wf4ever.checklist.client.EvaluationResult;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Utils;
import org.purl.wf4ever.rosrs.client.evo.JobStatus;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;
import org.purl.wf4ever.rosrs.client.notifications.Notification;
import org.purl.wf4ever.rosrs.client.notifications.NotificationService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.behaviors.EvolutionInfoLoadBehavior;
import pl.psnc.dl.wf4ever.portal.behaviors.FutureUpdateBehavior;
import pl.psnc.dl.wf4ever.portal.behaviors.JobStatusUpdatingBehaviour;
import pl.psnc.dl.wf4ever.portal.behaviors.RoLoadBehavior;
import pl.psnc.dl.wf4ever.portal.components.annotations.AdvancedAnnotationsPanel;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.events.MetadataDownloadEvent;
import pl.psnc.dl.wf4ever.portal.events.NotificationsLoadedEvent;
import pl.psnc.dl.wf4ever.portal.events.QualityEvaluatedEvent;
import pl.psnc.dl.wf4ever.portal.events.RoEvolutionLoadedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.AggregationChangedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.JobFinishedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseCreateEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseCreatedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotCreateEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotCreatedEvent;
import pl.psnc.dl.wf4ever.portal.modals.DownloadMetadataModal;
import pl.psnc.dl.wf4ever.portal.modals.ImportAnnotationModal;
import pl.psnc.dl.wf4ever.portal.pages.BasePage;
import pl.psnc.dl.wf4ever.portal.pages.Error404Page;
import pl.psnc.dl.wf4ever.portal.pages.ro.evo.RoEvoBox;
import pl.psnc.dl.wf4ever.portal.pages.ro.notifications.NotificationPreviewPanel;
import pl.psnc.dl.wf4ever.portal.pages.ro.notifications.NotificationsIndicator;
import pl.psnc.dl.wf4ever.portal.pages.ro.notifications.NotificationsList;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sun.jersey.api.client.ClientResponse;

/**
 * The Research Object page.
 * 
 * @author piotrekhol
 * 
 */
public class RoPage extends BasePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    static final Logger LOG = Logger.getLogger(RoPage.class);

    /** The feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** The research object. */
    protected ResearchObject researchObject;

    /** Checklist quality evaluation of this RO. */
    protected EvaluationResult qualityEvaluation;

    /** Template for HTML Link Headers. */
    private static final String HTML_LINK_TEMPLATE = "<link rel=\"%s\" href=\"%s\"/>";

    /** Notifications about this RO. */
    private List<Notification> notifications;

    /** Loadable event bus model. */
    private IModel<EventBus> eventBusModel;


    /**
     * Constructor.
     * 
     * @param parameters
     *            page parameters
     * @throws URISyntaxException
     *             if URIs returned by the RODL are incorrect
     */
    public RoPage(final PageParameters parameters)
            throws URISyntaxException {
        super(parameters);
        if (!parameters.get("ro").isEmpty()) {
            URI roURI = new URI(parameters.get("ro").toString());
            researchObject = new ResearchObject(roURI, MySession.get().getRosrs());
        } else {
            throw new RestartResponseException(Error404Page.class, new PageParameters().add("message",
                "The RO URI is missing."));
        }

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);
        if (!MySession.get().getRoles().contains(Roles.USER)) {
            info("<b>Sign in</b> to edit this research object.");
        }

        NotificationService notificationService = new NotificationService(getRodlURI(), null);
        ChecklistEvaluationService checklistService = ((PortalApplication) getApplication()).getChecklistService();

        IModel<ResearchObject> researchObjectModel = new Model<ResearchObject>(researchObject);
        this.setDefaultModel(researchObjectModel);
        IModel<List<Notification>> notificationsModel = new PropertyModel<List<Notification>>(this, "notifications");
        IModel<EvaluationResult> qualityModel = new PropertyModel<EvaluationResult>(this, "qualityEvaluation");
        String rssLink = notificationService.getNotificationsUri(researchObjectModel.getObject().getUri(), null, null)
                .toString();
        eventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        eventBusModel.getObject().register(this);

        add(new RoSummaryPanel("ro-summary", researchObjectModel, eventBusModel));
        add(new RoActionsPanel("ro-actions", researchObjectModel, eventBusModel));
        add(new NotificationsIndicator("notifications", researchObjectModel, notificationsModel, eventBusModel,
                rssLink, "notifications"));
        add(new QualityBar("health-progress-bar", qualityModel, eventBusModel));
        add(new RoCommentsPanel("comments", researchObjectModel, eventBusModel));
        add(new AdvancedAnnotationsPanel("advanced-annotations", "ro-basic-view", researchObjectModel, eventBusModel));
        add(new RoContentPanel("content", researchObjectModel, eventBusModel));
        add(new RoEvoBox("ro-evo-box", researchObjectModel, eventBusModel));

        CompoundPropertyModel<Notification> selectedNotification = new CompoundPropertyModel<Notification>(
                (Notification) null);
        add(new NotificationsList("notificationsList", notificationsModel, selectedNotification, eventBusModel));
        add(new NotificationPreviewPanel("notificationPanel", selectedNotification, eventBusModel));

        add(new DownloadMetadataModal("download-metadata-modal", eventBusModel));
        add(new ImportAnnotationModal("import-annotation-modal", eventBusModel));

        ExecutorService executor = Executors.newFixedThreadPool(10);
        Future<EvaluationResult> evaluateFuture = executor.submit(createChecklistEvaluationCallable(checklistService,
            researchObjectModel));
        Future<List<Notification>> notificationsFuture = executor.submit(createNotificationsCallable(
            notificationService, researchObjectModel));
        add(new FutureUpdateBehavior<>(Duration.seconds(1), evaluateFuture, qualityModel, eventBusModel,
                QualityEvaluatedEvent.class));
        add(new FutureUpdateBehavior<>(Duration.seconds(1), notificationsFuture, notificationsModel, eventBusModel,
                NotificationsLoadedEvent.class));
        add(new RoLoadBehavior(feedbackPanel, researchObjectModel, eventBusModel));
        add(new EvolutionInfoLoadBehavior(feedbackPanel, researchObjectModel, eventBusModel));
    }


    /**
     * Create a new task of calculating the RO quality that can be scheduled for later.
     * 
     * @param service
     *            checklist evaluation service
     * @param model
     *            RO model
     * @return a new {@link Callable}
     */
    private Callable<EvaluationResult> createChecklistEvaluationCallable(final ChecklistEvaluationService service,
            final IModel<ResearchObject> model) {
        return new Callable<EvaluationResult>() {

            @Override
            public EvaluationResult call()
                    throws Exception {
                return service.evaluate(model.getObject().getUri(), "ready-to-release");
            }
        };
    }


    /**
     * Create a new task of loading the notifications that can be scheduled for later.
     * 
     * @param notificationService
     *            notification service
     * @param model
     *            RO model
     * @return a new {@link Callable}
     */
    private Callable<List<Notification>> createNotificationsCallable(final NotificationService notificationService,
            final IModel<ResearchObject> model) {
        return new Callable<List<Notification>>() {

            @Override
            public List<Notification> call()
                    throws Exception {
                return notificationService.getNotifications(model.getObject().getUri(), null, null);
            }
        };
    }


    /**
     * Start the snapshot creation process.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void createSnapshot(SnapshotCreateEvent event) {
        final JobStatus status = researchObject.snapshot(researchObject.getName().substring(0,
            researchObject.getName().length() - 1)
                + "-snapshot");
        feedbackPanel.add(new JobStatusUpdatingBehaviour(feedbackPanel, status, "snapshot", eventBusModel,
                SnapshotCreatedEvent.class));
        event.getTarget().add(feedbackPanel);
    }


    /**
     * Start the release creation process.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void createArchive(ReleaseCreateEvent event) {
        final JobStatus status = researchObject.archive(researchObject.getName().substring(0,
            researchObject.getName().length() - 1)
                + "-release");
        feedbackPanel.add(new JobStatusUpdatingBehaviour(feedbackPanel, status, "release", eventBusModel,
                ReleaseCreatedEvent.class));
        event.getTarget().add(feedbackPanel);
    }


    /**
     * Reload the evolution information when a snapshot or release is created.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onJobFinished(JobFinishedEvent event) {
        researchObject.loadEvolutionInformation();
        eventBusModel.getObject().post(new RoEvolutionLoadedEvent(event.getTarget()));
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        try {
            ClientResponse head = MySession.get().getRosrs().getResourceHead(researchObject.getUri());
            List<String> headers = head.getHeaders().get("Link");
            if (headers != null && !headers.isEmpty()) {
                Multimap<String, URI> links = Utils.getLinkHeaders(headers);
                for (Entry<String, URI> link : links.entries()) {
                    response.renderString(String.format(HTML_LINK_TEMPLATE, link.getKey(), link.getValue()));
                }
            }
            head.close();
        } catch (Exception e) {
            LOG.error("Unexpected response when getting RO head", e);
            throw new RestartResponseException(Error404Page.class, new PageParameters().add("message", "The RO "
                    + researchObject.getUri() + " appears to be incorrect."));
        }
    }


    public EvaluationResult getQualityEvaluation() {
        return qualityEvaluation;
    }


    public List<Notification> getNotifications() {
        return notifications;
    }


    /**
     * Redirect to the metadata file.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onMetadataDownload(MetadataDownloadEvent event) {
        event.getTarget().appendJavaScript("window.location.href='" + getROMetadataLink(event.getFormat()) + "'");
    }


    /**
     * When the aggregation has changed, recalculate the quality.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAggregationChanged(AggregationChangedEvent event) {
        ChecklistEvaluationService service = ((PortalApplication) getApplication()).getChecklistService();
        IModel<EvaluationResult> qualityModel = new PropertyModel<EvaluationResult>(this, "qualityEvaluation");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        @SuppressWarnings("unchecked")
        Future<EvaluationResult> evaluateFuture = executor.submit(createChecklistEvaluationCallable(service,
            (IModel<ResearchObject>) this.getDefaultModel()));
        FutureUpdateBehavior<EvaluationResult> behavior = new FutureUpdateBehavior<>(Duration.seconds(1),
                evaluateFuture, qualityModel, eventBusModel, QualityEvaluatedEvent.class);
        this.add(behavior);
        event.getTarget().appendJavaScript(behavior.getCallbackScript());
    }


    /**
     * Called when an annotation body has been uploaded.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onAnnotationImport(ImportAnnotationReadyEvent event) {
        String contentType = RDFFormat.forFileName(event.getUploadedFile().getClientFileName(), RDFFormat.RDFXML)
                .getDefaultMIMEType();
        Annotable annotable = event.getAnnotableModel().getObject();
        try {
            annotable.annotate(event.getUploadedFile().getClientFileName(), event.getUploadedFile().getInputStream(),
                contentType);
        } catch (ROSRSException | ROException | IOException e) {
            error(e.getMessage());
            LOG.error("Can't import annotations", e);
        }
        eventBusModel.getObject().post(new AnnotationAddedEvent(event.getTarget(), event.getAnnotableModel()));
    }


    public String getROZipLink() {
        return researchObject.getUri().toString().replaceFirst("/ROs/", "/zippedROs/");
    }


    /**
     * Return a link to a format-specific version of the manifest.
     * 
     * @param format
     *            RDF format
     * @return a URI as string of the resource
     */
    public String getROMetadataLink(RDFFormat format) {
        return researchObject.getUri()
                .resolve(".ro/manifest." + format.getDefaultFileExtension() + "?original=manifest.rdf").toString();
    }

}
