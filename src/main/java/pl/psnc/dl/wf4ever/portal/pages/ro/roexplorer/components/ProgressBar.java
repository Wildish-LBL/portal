package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
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
    private Label label;


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
        label = new Label("label");
        add(label);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(PROGRESSBAR_CLASS_REFERENCE);
        response.renderOnDomReadyJavaScript("setValue(50);");
        if (getDefaultModelObject() != null) {
        }
    }


    public void onQualityEvaluated(AjaxRequestTarget target) {
        label.setVisible(false);
        target.add(this);
    }
}
