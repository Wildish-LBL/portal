package pl.psnc.dl.wf4ever.portal.components.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.purl.wf4ever.rosrs.client.Annotable;
import org.purl.wf4ever.rosrs.client.Annotation;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.AnnotationTripleByDateComparator;

import pl.psnc.dl.wf4ever.portal.components.form.EditableTextPanel;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AbstractAnnotationEditedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.CommentAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * A list of comments and a field for adding a new one.
 * 
 * @author piotrekhol
 * 
 */
public class CommentsList extends Panel {

    /**
     * The list of comments.
     * 
     * @author piotrekhol
     * 
     */
    private final class CommentsListView extends ListView<AnnotationTriple> {

        /** id. */
        private static final long serialVersionUID = 8955964609121617316L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket ID
         * @param listModel
         *            the model of the list of comments
         */
        private CommentsListView(String id, IModel<List<AnnotationTriple>> listModel) {
            super(id, listModel);
        }


        @Override
        protected void populateItem(ListItem<AnnotationTriple> item) {
            AnnotationTripleModel model = new AnnotationTripleModel(item.getModelObject());
            item.add(new EditableCommentTextPanel("comment", model, internalEventBusModel));
        }
    }


    /**
     * A model that generates a list of quads representing comments, sorted by date.
     * 
     * @author piotrekhol
     * 
     */
    private final class CommentsToListModel extends LoadableDetachableModel<List<AnnotationTriple>> {

        /** id. */
        private static final long serialVersionUID = 2133793540209922573L;

        /** Model of the resource that the comments should be about. */
        private IModel<? extends Annotable> annotableModel;


        /**
         * Constructor.
         * 
         * @param annotableModel
         *            Model of the resource that the comments should be about
         */
        public CommentsToListModel(IModel<? extends Annotable> annotableModel) {
            this.annotableModel = annotableModel;
        }


        @Override
        protected List<AnnotationTriple> load() {
            List<AnnotationTriple> list = new ArrayList<>();
            if (annotableModel.getObject() != null) {
                for (Entry<Annotation, String> e : annotableModel.getObject().getPropertyValues(RDFS.comment)
                        .entrySet()) {
                    list.add(new AnnotationTriple(e.getKey(), annotableModel.getObject(), RDFS.comment, e.getValue()));
                }
                Collections.sort(list, new AnnotationTripleByDateComparator());
            }
            return list;
        }
    }


    /**
     * The field for adding a new comment.
     * 
     * @author piotrekhol
     * 
     */
    private final class AddCommentPanel extends WebMarkupContainer {

        /** id. */
        private static final long serialVersionUID = -8532057319454880146L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket ID
         * @param model
         *            the model for the comment value
         * @param eventBusModel
         *            event bus model
         * @param target
         *            AJAX target to which to add the new panel
         */
        public AddCommentPanel(String id, AnnotationTripleModel model, IModel<EventBus> eventBusModel,
                AjaxRequestTarget target) {
            super(id);
            setOutputMarkupId(true);
            setOutputMarkupPlaceholderTag(true);
            add(new EditableTextPanel("new-comment", model, eventBusModel, true, true));
        }

    }


    /** id. */
    private static final long serialVersionUID = 9083555161289351913L;

    /** a list of comments. */
    private ListView<AnnotationTriple> comments;

    /** a field for adding a new comment. */
    private WebMarkupContainer addCommentPanel;

    /** event bus model for button clicks. */
    private LoadableDetachableModel<EventBus> internalEventBusModel;

    /** a panel with a message if there are no comments. */
    private WebMarkupContainer noComments;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket ID
     * @param annotableModel
     *            the model of the resource that the comments are about
     * @param eventBusModel
     *            event bus model for the event of adding/deleting comments
     */
    public CommentsList(String id, final IModel<? extends Annotable> annotableModel,
            final IModel<EventBus> eventBusModel) {
        super(id, annotableModel);
        eventBusModel.getObject().register(this);
        setOutputMarkupPlaceholderTag(true);
        IModel<List<AnnotationTriple>> listModel = new CommentsToListModel(annotableModel);
        noComments = new WebMarkupContainer("no-comments");
        add(noComments);
        comments = new CommentsListView("list", listModel);
        add(comments);

        internalEventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        internalEventBusModel.getObject().register(this);

        addCommentPanel = new WebMarkupContainer("add-comment");
        addCommentPanel.setOutputMarkupPlaceholderTag(true);
        addCommentPanel.setVisible(false);
        add(addCommentPanel);
    }


    /**
     * Called when the button is clicked to show the panel for the new comment.
     * 
     * @param event
     *            the AJAX event
     */
    @Subscribe
    public void onAddCommentClicked(CommentAddClickedEvent event) {
        if (event.getAnnotableModel().getObject() == this.getDefaultModelObject()) {
            AnnotationTripleModel model = new AnnotationTripleModel(null, (Annotable) this.getDefaultModelObject(),
                    RDFS.comment);
            AddCommentPanel panel = new AddCommentPanel("add-comment", model, internalEventBusModel, event.getTarget());
            panel.setVisible(true);
            addCommentPanel.replaceWith(panel);
            addCommentPanel = panel;
            event.getTarget().add(addCommentPanel);
        }
    }


    @Override
    protected void onConfigure() {
        noComments.setVisible(comments.getModelObject().isEmpty());
        super.onConfigure();
    }


    /**
     * Called when the user accepted or cancelled a new comment.
     * 
     * @param event
     *            the AJAX event
     */
    @Subscribe
    public void onCommentAdded(AbstractAnnotationEditedEvent event) {
        addCommentPanel.setVisible(false);
        event.getTarget().add(this);
    }


    /**
     * Called when the current resource has changed.
     * 
     * @param event
     *            the AJAX event
     */
    @Subscribe
    public void onResourceSelected(ResourceSelectedEvent event) {
        event.getTarget().add(this);
    }

}
