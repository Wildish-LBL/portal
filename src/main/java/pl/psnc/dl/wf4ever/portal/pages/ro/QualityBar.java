package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.purl.wf4ever.checklist.client.ChecklistItem;
import org.purl.wf4ever.checklist.client.EvaluationResult;

import pl.psnc.dl.wf4ever.portal.events.QualityEvaluatedEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Health progress bar.
 * 
 * @author pejot
 * 
 */
public class QualityBar extends Panel {

    /** id. */
    private static final long serialVersionUID = -8244521183370538171L;

    /** JavaScript for drawing the progress bar. */
    private static final JavaScriptResourceReference PROGRESSBAR_CLASS_REFERENCE = new JavaScriptResourceReference(
            QualityBar.class, "health-progress-bar.js");

    /** The label used when the bar is not ready. */
    private WebMarkupContainer label;

    /** The progress bar. */
    private WebMarkupContainer bar;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            RO health (0-100)
     * @param eventBusModel
     *            event bus model
     */
    public QualityBar(String id, IModel<EvaluationResult> model, IModel<EventBus> eventBusModel) {
        super(id, model);
        eventBusModel.getObject().register(this);
        setOutputMarkupId(true);
        label = new WebMarkupContainer("initialLabel");
        add(label);
        bar = new WebMarkupContainer("progressBar");
        add(bar);
        bar.setVisible(false);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(PROGRESSBAR_CLASS_REFERENCE);
        if (getDefaultModelObject() != null) {
            EvaluationResult result = (EvaluationResult) getDefaultModelObject();
            response.renderOnDomReadyJavaScript("setValue(" + result.getEvaluationScore() + ");");
        }
    }


    /**
     * Draw the progress bar once the quality has been calculated.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onQualityEvaluated(QualityEvaluatedEvent event) {
        if (getDefaultModelObject() != null) {
            EvaluationResult result = (EvaluationResult) getDefaultModelObject();
            label.setVisible(false);
            bar.setVisible(true);
            bar.add(new AttributeModifier("data-title", "Details"));
            String details = getDetailsHtml(result);
            bar.add(new AttributeModifier("data-content", details));
        }
        event.getTarget().add(this);
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
}
