/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.components.feedback;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;

/**
 * A component feedback panel with an improved HTML.
 * 
 * @author piotrhol
 * 
 */
public class MyComponentFeedbackPanel extends ComponentFeedbackPanel {

    /** id. */
    private static final long serialVersionUID = 2434655818879345187L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param filter
     *            filter
     */
    public MyComponentFeedbackPanel(String id, Component filter) {
        super(id, filter);
    }

}
