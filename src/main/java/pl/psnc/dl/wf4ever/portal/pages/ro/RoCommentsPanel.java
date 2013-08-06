package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.annotations.CommentsList;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.annotations.CommentAddClickedEvent;

/**
 * Panel aggregating the comments of an RO and buttons to change it.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class RoCommentsPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RoCommentsPanel.class);

    /** Form aggrgeating the buttons. */
    private Form<Void> form;

    /** A list of comments. */
    private CommentsList commentsList;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the research object model
     */
    public RoCommentsPanel(String id, final IModel<ResearchObject> model) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);

        form = new Form<Void>("form");
        add(form);
        form.add(new AnnotationEditAjaxEventButton("add-comment", form, model, null, CommentAddClickedEvent.class));

        commentsList = new CommentsList("comments-list", model);
        add(commentsList);
    }
}
