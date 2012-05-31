/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.util;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;

/**
 * @author piotrhol
 * 
 */
public class MyComponentFeedbackPanel extends ComponentFeedbackPanel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2434655818879345187L;


    public MyComponentFeedbackPanel(String id, Component filter) {
        super(id, filter);
    }

}
