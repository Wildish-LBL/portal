package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.purl.wf4ever.checklist.client.EvaluationResult;

/**
 * Health progress bar.
 * 
 * @author pejot
 * 
 */
public class ProgressBar extends Panel {

    /** id. */
    private static final long serialVersionUID = -8244521183370538171L;
    private static final JavaScriptResourceReference PROGRESSBAR_CLASS_REFERENCE = new JavaScriptResourceReference(
            RoTree.class, "health-progress-bar.js");
    private WebMarkupContainer label;
    private WebMarkupContainer bar;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            RO health (0-100)
     */
    public ProgressBar(String id, IModel<EvaluationResult> model) {
        super(id, model);
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


    public void onQualityEvaluated(AjaxRequestTarget target) {
        if (getDefaultModelObject() != null) {
            label.setVisible(false);
            bar.setVisible(true);
        }
        target.add(this);
    }
}
