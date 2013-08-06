package pl.psnc.dl.wf4ever.portal.components.annotations;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Annotation;

import pl.psnc.dl.wf4ever.portal.components.form.EditableTextPanel;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTimestampModel;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTripleModel;

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
     */
    public EditableCommentTextPanel(String id, AnnotationTripleModel model) {
        super(id, model, true, false);
    }


    @Override
    protected Fragment newViewFragment(AnnotationTripleModel model) {
        return new CommentViewFragment("content", "view", this, model);

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
         */
        public CommentViewFragment(String id, String markupId, MarkupContainer markupProvider,
                AnnotationTripleModel model) {
            super(id, markupId, markupProvider, model);
            form.add(new Label("authorDate", new AnnotationTimestampModel(new PropertyModel<Annotation>(model
                    .getObject(), "annotation"))));
        }

    }

}
