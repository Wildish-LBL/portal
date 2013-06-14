package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.annotations.CommentsList;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.RoLoadedEvent;
import pl.psnc.dl.wf4ever.portal.events.ShowAllAnnotationsEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotateEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.CommentAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationClickedEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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
     * @param eventBusModel
     *            event bus model for button clicks
     */
    public RoCommentsPanel(String id, final IModel<ResearchObject> model, IModel<EventBus> eventBusModel) {
        super(id, model);
        eventBusModel.getObject().register(this);

        form = new Form<Void>("form");
        add(form);
        form.add(new AnnotationEditAjaxEventButton("add-comment", form, model, eventBusModel,
                CommentAddClickedEvent.class));
        form.add(new AnnotationEditAjaxEventButton("import-annotations", form, model, eventBusModel,
                ImportAnnotationClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("annotate", form, eventBusModel, AnnotateEvent.class));
        form.add(new AjaxEventButton("show-all", form, eventBusModel, ShowAllAnnotationsEvent.class));

        commentsList = new CommentsList("comments-list", model, eventBusModel);
        eventBusModel.getObject().register(commentsList);
        add(commentsList);
    }


    /**
     * Refresh the comments list when the RO is loaded.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onRoLoaded(RoLoadedEvent event) {
        event.getTarget().add(commentsList);
    }


    @Subscribe
    public void onAnnotateClicked(AnnotateEvent event) {
        //TODO
        System.out.println("annotate");
    }


    @Subscribe
    public void onShowAllAnnotationsClicked(ShowAllAnnotationsEvent event) {
        //TODO
        System.out.println("show all");
    }

}
