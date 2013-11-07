package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.purl.wf4ever.checklist.client.EvaluationResult;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.evo.JobStatus;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;
import org.purl.wf4ever.rosrs.client.notifications.Notification;
import org.purl.wf4ever.rosrs.client.notifications.NotificationService;
import org.purl.wf4ever.rosrs.client.users.User;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.behaviors.FutureUpdateBehavior;
import pl.psnc.dl.wf4ever.portal.behaviors.JobStatusUpdatingBehaviour;
import pl.psnc.dl.wf4ever.portal.components.LoadingCircle;
import pl.psnc.dl.wf4ever.portal.components.annotations.AdvancedAnnotationsPanel;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.events.ErrorEvent;
import pl.psnc.dl.wf4ever.portal.events.MetadataDownloadEvent;
import pl.psnc.dl.wf4ever.portal.events.RoEvolutionLoadedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotationAddedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.JobFinishedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseCreateEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseCreatedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotCreateEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotCreatedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.SketchEvent;
import pl.psnc.dl.wf4ever.portal.modals.DownloadMetadataModal;
import pl.psnc.dl.wf4ever.portal.modals.ImportAnnotationModal;
import pl.psnc.dl.wf4ever.portal.pages.ro.evo.RoEvoBox;
import pl.psnc.dl.wf4ever.portal.pages.ro.notifications.NotificationPreviewPanel;
import pl.psnc.dl.wf4ever.portal.pages.ro.notifications.NotificationsIndicator;
import pl.psnc.dl.wf4ever.portal.pages.ro.notifications.NotificationsList;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

/**
 * A panel with all RO data.
 * 
 * @author piotrekhol
 * 
 */
public class RoPanel extends Panel {

	/** id. */
	private static final long serialVersionUID = -3516631869043579533L;

	/** Logger. */
	static final Logger LOG = Logger.getLogger(RoPanel.class);

	/** The feedback panel. */
	private MyFeedbackPanel feedbackPanel;

	/** Import annotations modal. */
	private ImportAnnotationModal importAnnotationsModal;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            wicket ID
	 * @param researchObjectModel
	 *            RO model
	 */
	public RoPanel(String id, IModel<ResearchObject> researchObjectModel) {
		super(id, researchObjectModel);
		PortalApplication app = (PortalApplication) getApplication();
		feedbackPanel = new MyFeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		add(feedbackPanel);
		MySession session = MySession.get();
		if (!MySession.get().getRoles().contains(Roles.USER)) {
			info("<b>Sign in</b> to edit this research object.");
		}

		try {
			researchObjectModel.getObject().load();
		} catch (ROSRSException | ROException e) {
			error("Research object cannot be loaded: " + e.getMessage());
			RoPage.LOG.error("Research object cannot be loaded", e);
		}

		NotificationService notificationService = new NotificationService(app.getRodlURI(), null);

		IModel<ArrayList<Notification>> notificationsModel = new Model<ArrayList<Notification>>();
		IModel<EvaluationResult> qualityModel = new Model<EvaluationResult>();
		String rssLink = notificationService.getNotificationsUri(
				researchObjectModel.getObject().getUri(), null, null).toString();

		add(new RoSummaryPanel("ro-summary", researchObjectModel));
		add(new RoActionsPanel("ro-actions", researchObjectModel));
		NotificationsIndicator notificationsIndicator = new NotificationsIndicator("notifications",
				researchObjectModel, notificationsModel, rssLink, "notifications");
		add(notificationsIndicator);
		QualityBar qualityBar = new QualityBar("health-progress-bar", qualityModel,
				researchObjectModel, app.getDefaultMinimModel());
		add(qualityBar);
		add(new RoCommentsPanel("comments", researchObjectModel));
		add(new AdvancedAnnotationsPanel("advanced-annotations", "ro-basic-view",
				researchObjectModel));
		add(new RoContentPanel("content", researchObjectModel));
		add(new AjaxLazyLoadPanel("ro-evo-box", researchObjectModel) {

			/** id. */
			private static final long serialVersionUID = 3059220547438504606L;

			@SuppressWarnings("unchecked")
			@Override
			public Component getLazyLoadComponent(String markupId) {
				return new RoEvoBox(markupId, (IModel<ResearchObject>) getDefaultModel());
			}

			@Override
			public Component getLoadingComponent(String markupId) {
				return new LoadingCircle(markupId, "Loading research object evolution metadata...");
			}
		});

		add(new QualityPanel("quality-panel", researchObjectModel, app.getChecklistService(),
				app.getMinimModels()));
		add(new RelationsPanel("relations-panel", researchObjectModel));
		final User user = session.getUser();
		Panel accessControlPanel = null;
		// should be if user is an owner... is't difficult to check I think
		if (user != null) {
			accessControlPanel = new AccessControlPanel("accesscontrol-panel", researchObjectModel);
		} else {
			accessControlPanel = new IvisibleAccessControlPanel("accesscontrol-panel",
					researchObjectModel);
			accessControlPanel.setVisible(false);
		}
		add(accessControlPanel);
		IModel<Notification> selectedNotification = new Model<Notification>((Notification) null);
		NotificationsList notificationsList = new NotificationsList("notificationsList",
				notificationsModel, selectedNotification);
		add(notificationsList);
		add(new NotificationPreviewPanel("notificationPanel", selectedNotification));

		add(new DownloadMetadataModal("download-metadata-modal"));
		importAnnotationsModal = new ImportAnnotationModal("import-annotation-modal",
				researchObjectModel);
		add(importAnnotationsModal);

		ExecutorService executor = Executors.newFixedThreadPool(10);
		Future<ArrayList<Notification>> notificationsFuture = executor.submit(RoPage
				.createNotificationsCallable(notificationService, researchObjectModel));
		add(new FutureUpdateBehavior<ArrayList<Notification>>(Duration.seconds(1),
				session.storeObject(notificationsFuture), notificationsModel,
				notificationsIndicator, notificationsList));
	}

	@Override
	public void onEvent(IEvent<?> event) {
		super.onEvent(event);
		if (event.getPayload() instanceof ImportAnnotationClickedEvent) {
			onImportAnnotationsClicked((ImportAnnotationClickedEvent) event.getPayload());
		}
		if (event.getPayload() instanceof SnapshotCreateEvent) {
			createSnapshot((SnapshotCreateEvent) event.getPayload());
		}
		if (event.getPayload() instanceof ReleaseCreateEvent) {
			createArchive((ReleaseCreateEvent) event.getPayload());
		}
		if (event.getPayload() instanceof JobFinishedEvent) {
			onJobFinished((JobFinishedEvent) event.getPayload());
		}
		if (event.getPayload() instanceof MetadataDownloadEvent) {
			onMetadataDownload((MetadataDownloadEvent) event.getPayload());
		}
		if (event.getPayload() instanceof ImportAnnotationReadyEvent) {
			onAnnotationImport((ImportAnnotationReadyEvent) event.getPayload());
		}
		if (event.getPayload() instanceof ErrorEvent) {
			onError((ErrorEvent) event.getPayload());
		}
		if (event.getPayload() instanceof SketchEvent) {
			onSketchEvent((SketchEvent) event.getPayload());
		}
	}

	/**
	 * Display the modal.
	 * 
	 * @param event
	 *            AJAX event
	 */
	private void onImportAnnotationsClicked(ImportAnnotationClickedEvent event) {
		ImportAnnotationModal importAnnotationsModal2 = new ImportAnnotationModal(
				"import-annotation-modal", event.getAnnotableModel());
		importAnnotationsModal.replaceWith(importAnnotationsModal2);
		importAnnotationsModal = importAnnotationsModal2;
		importAnnotationsModal.show(event.getTarget());
	}

	/**
	 * Start the snapshot creation process.
	 * 
	 * @param event
	 *            AJAX event
	 */
	private void createSnapshot(SnapshotCreateEvent event) {
		ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
		final JobStatus status = researchObject.snapshot(researchObject.getName().substring(0,
				researchObject.getName().length() - 1)
				+ "-snapshot");
		feedbackPanel.add(new JobStatusUpdatingBehaviour(status, "snapshot",
				SnapshotCreatedEvent.class));
		event.getTarget().add(feedbackPanel);
	}

	/**
	 * Start the release creation process.
	 * 
	 * @param event
	 *            AJAX event
	 */
	private void createArchive(ReleaseCreateEvent event) {
		ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
		final JobStatus status = researchObject.archive(researchObject.getName().substring(0,
				researchObject.getName().length() - 1)
				+ "-release");
		feedbackPanel.add(new JobStatusUpdatingBehaviour(status, "release",
				ReleaseCreatedEvent.class));
		event.getTarget().add(feedbackPanel);
	}

	/**
	 * Reload the evolution information when a snapshot or release is created.
	 * 
	 * @param event
	 *            AJAX event
	 */
	private void onJobFinished(JobFinishedEvent event) {
		ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
		researchObject.loadEvolutionInformation();
		send(getPage(), Broadcast.BREADTH, new RoEvolutionLoadedEvent(event.getTarget()));
	}

	private void onSketchEvent(SketchEvent event) {
		event.getTarget().add(this.get("ro-summary"));
		this.get("ro-summary").configure();
	}

	/**
	 * Redirect to the metadata file.
	 * 
	 * @param event
	 *            AJAX event
	 */
	private void onMetadataDownload(MetadataDownloadEvent event) {
		event.getTarget().appendJavaScript(
				"window.location.href='" + getROMetadataLink(event.getFormat()) + "'");
	}

	/**
	 * Called when an annotation body has been uploaded.
	 * 
	 * @param event
	 *            AJAX event
	 */
	private void onAnnotationImport(ImportAnnotationReadyEvent event) {
		String contentType = RDFFormat.forFileName(event.getUploadedFile().getClientFileName(),
				RDFFormat.RDFXML).getDefaultMIMEType();
		Annotable annotable = event.getAnnotableModel().getObject();
		try {
			annotable.annotate(event.getUploadedFile().getClientFileName(), event.getUploadedFile()
					.getInputStream(), contentType);
		} catch (ROSRSException | ROException | IOException e) {
			error(e.getMessage());
			LOG.error("Can't import annotations", e);
		}
		send(getPage(), Broadcast.BREADTH,
				new AnnotationAddedEvent(event.getTarget(), event.getAnnotableModel()));
	}

	/**
	 * Return the link to the ZIP archive.
	 * 
	 * @return URI of the ZIP archive
	 */
	public String getROZipLink() {
		ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
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
		ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
		return researchObject
				.getUri()
				.resolve(
						".ro/manifest." + format.getDefaultFileExtension()
								+ "?original=manifest.rdf").toString();
	}

	/**
	 * Refresh feedback panel in case of error.
	 * 
	 * @param event
	 *            AJAX event
	 */
	private void onError(ErrorEvent event) {
		event.getTarget().add(feedbackPanel);
	}

}
