package pl.psnc.dl.wf4ever.portal.components.annotations;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Annotation;

import pl.psnc.dl.wf4ever.portal.components.form.EditableTextPanel;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;

import com.google.common.eventbus.EventBus;

/**
 * A panel for inline comments, show the annotation author and creation date.
 * 
 * @author piotrekhol
 * 
 */
public class EditableCommentTextPanel extends EditableTextPanel {

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EditableCommentTextPanel.class);

    /** id. */
    private static final long serialVersionUID = 1L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the model for the quad
     * @param eventBusModel
     *            event bus model for when a comment is added, deleted or edited
     */
    public EditableCommentTextPanel(String id, AnnotationTripleModel model, final IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel, true, false);
    }


    @Override
    protected Fragment newViewFragment(IModel<String> model, IModel<EventBus> internalEventBusModel) {
        return new CommentViewFragment("content", "view", this, model, internalEventBusModel);

    }


    /**
     * The preview of the comment.
     * 
     * @author piotrekhol
     * 
     */
    class CommentViewFragment extends ViewFragment {

        /** id. */
        private static final long serialVersionUID = 3200222524752594671L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket ID
         * @param markupId
         *            the fragment wicket ID
         * @param markupProvider
         *            the location of the fragment
         * @param model
         *            the comment value model
         * @param internalEventBusModel
         *            the event bus for the button clicks
         */
        public CommentViewFragment(String id, String markupId, MarkupContainer markupProvider, IModel<String> model,
                IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider, model, internalEventBusModel);
            Annotation ann = ((AnnotationTripleModel) model).getAnnotationTriple().getAnnotation();
            form.add(new Label("author", new PropertyModel<String>(ann, "author.name")));
            form.add(new Label("created", new PropertyModel<String>(ann, "createdFormatted")));
        }

    }

}
