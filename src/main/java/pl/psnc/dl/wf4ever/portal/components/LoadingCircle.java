package pl.psnc.dl.wf4ever.portal.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Simple loading image.
 * 
 * @author pejot
 * 
 */

public class LoadingCircle extends Panel {

    /** Serializable. */
    private static final long serialVersionUID = 1L;

    /** Text displayed when the object is loaded. */
    Label loadingLabel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param loadingText
     *            displayed text
     */
    public LoadingCircle(String id, String loadingText) {
        super(id);
    }
}
