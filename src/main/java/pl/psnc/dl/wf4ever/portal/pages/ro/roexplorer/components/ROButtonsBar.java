package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Buttons bar for selected folder or resource.
 * 
 * @author pejot
 * 
 */
public class ROButtonsBar extends Panel {

    /** Serialziation. */
    private static final long serialVersionUID = 1L;
    /** Download RO metadata button. */
    private AjaxButton downloadROMetadata;

    /** Download RO ZIP button. */
    private ExternalLink downloadROZipped;

    /** Fake form for ajax buttons. */
    Form<?> roForm;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public ROButtonsBar(String id) {
        super(id);
        setOutputMarkupId(true);
        roForm = new Form<Void>("roForm");
    }

}
