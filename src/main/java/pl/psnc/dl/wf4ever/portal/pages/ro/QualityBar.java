package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.time.Duration;
import org.purl.wf4ever.checklist.client.ChecklistEvaluationService;
import org.purl.wf4ever.checklist.client.ChecklistItem;
import org.purl.wf4ever.checklist.client.EvaluationResult;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.behaviors.FutureUpdateBehavior;
import pl.psnc.dl.wf4ever.portal.events.aggregation.AggregationChangedEvent;

/**
 * Health progress bar.
 * 
 * @author pejot
 * 
 */
public class QualityBar extends Panel {

    /** id. */
    private static final long serialVersionUID = -8244521183370538171L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(QualityBar.class);

    /** JavaScript for drawing the progress bar. */
    private static final JavaScriptResourceReference PROGRESSBAR_CLASS_REFERENCE = new JavaScriptResourceReference(
            QualityBar.class, "health-progress-bar.js");

    /** The label used when the bar is not ready. */
    private WebMarkupContainer label;

    /** The progress bar. */
    private WebMarkupContainer bar;

    /** RO model for quality calculation. */
    private IModel<ResearchObject> researchObjectModel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            RO health (0-100)
     * @param researchObjectModel
     *            RO model for quality calculation
     */
    public QualityBar(String id, IModel<EvaluationResult> model, IModel<ResearchObject> researchObjectModel) {
        super(id, model);
        this.researchObjectModel = researchObjectModel;
        setOutputMarkupId(true);
        label = new WebMarkupContainer("initialLabel");
        add(label);
        bar = new WebMarkupContainer("progressBar");
        add(bar);
        bar.setVisible(false);
        add(getRecalculationBehavior());
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(PROGRESSBAR_CLASS_REFERENCE));
        if (getDefaultModelObject() != null) {
            EvaluationResult result = (EvaluationResult) getDefaultModelObject();
            response.render(OnDomReadyHeaderItem.forScript("setValue(" + result.getEvaluationScore() + ");"));
        }
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (getDefaultModelObject() != null) {
            EvaluationResult result = (EvaluationResult) getDefaultModelObject();
            label.setVisible(false);
            bar.setVisible(true);
            bar.add(new AttributeModifier("data-title", "Details"));
            String details = getDetailsHtml(result);
            bar.add(new AttributeModifier("data-content", details));
        }
    }


    /**
     * Calculate a summary of checklist results as HTML.
     * 
     * @param result
     *            the evaluation result
     * @return the HTML
     */
    private String getDetailsHtml(EvaluationResult result) {
        StringBuilder sb = new StringBuilder();
        for (ChecklistItem item : result.getChecklistItems()) {
            sb.append("<p>");
            if (item.isItemSatisfied()) {
                sb.append("<img src='images/ok.png'> ");
            } else {
                sb.append("<img src='images/wrong.png'> ");
            }
            sb.append(item.getItemLabel());
            sb.append("</p>");
        }
        return sb.toString();
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
                if (service == null) {
                    LOGGER.error("Checklist evaluation service is null");
                    return null;
                }
                return service.evaluate(model.getObject().getUri(), "ready-to-release");
            }
        };
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof AggregationChangedEvent) {
            onAggregationChanged((AggregationChangedEvent) event.getPayload());
        }
    }


    /**
     * When the aggregation has changed, recalculate the quality.
     * 
     * @param event
     *            AJAX event
     */
    private void onAggregationChanged(AggregationChangedEvent event) {
        FutureUpdateBehavior<EvaluationResult> behavior = getRecalculationBehavior();
        this.add(behavior);
        event.getTarget().appendJavaScript(behavior.getCallbackScript());
    }


    /**
     * Create a behavior that will (re)calculate the RO quality.
     * 
     * @return a {@link FutureUpdateBehavior}
     */
    @SuppressWarnings("unchecked")
    private FutureUpdateBehavior<EvaluationResult> getRecalculationBehavior() {
        ChecklistEvaluationService service = ((PortalApplication) getApplication()).getChecklistService();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Future<EvaluationResult> evaluateFuture = executor.submit(createChecklistEvaluationCallable(service,
            researchObjectModel));
        FutureUpdateBehavior<EvaluationResult> behavior = new FutureUpdateBehavior<EvaluationResult>(
                Duration.seconds(1), MySession.get().addFuture(evaluateFuture),
                (IModel<EvaluationResult>) getDefaultModel(), this);
        return behavior;
    }

}
