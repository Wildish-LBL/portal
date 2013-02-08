package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
        add(new AbstractDefaultAjaxBehavior() {

            private static final long serialVersionUID = 1L;


            @Override
            protected void respond(AjaxRequestTarget target) {
            }


            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.renderJavaScriptReference(PROGRSSBAR_CLASS_REFRENCE);
            }
        });
    }

}
