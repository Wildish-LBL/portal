package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Health progress bar.
 * 
 * @author pejot
 * 
 */
public class ProgressBar extends Panel {

    /** id. */
    private static final long serialVersionUID = 6712353987699580159L;

    /** JS with progress bar initialization. */
    private static final JavaScriptResourceReference PROGRSSBAR_CLASS_REFRENCE = new JavaScriptResourceReference(
            RoTree.class, "health-progress-bar.js");


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @value RO health (1-100)
     */
    public ProgressBar(String id, Integer value) {
        super(id);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderJavaScriptReference(PROGRSSBAR_CLASS_REFRENCE);
    }

}
