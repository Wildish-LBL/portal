package pl.psnc.dl.wf4ever.portal.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotation;

/**
 * This model returns a creator and creation date, i.e. John Doe on Tuesday.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTimestampModel extends AbstractReadOnlyModel<String> {

    /** id. */
    private static final long serialVersionUID = -5110228026110644007L;

    /** Annotation model. */
    private IModel<Annotation> model;


    /**
     * Constructor.
     * 
     * @param model
     *            Annotation model
     */
    public AnnotationTimestampModel(IModel<Annotation> model) {
        this.model = model;
    }


    @Override
    public String getObject() {
        return model.getObject() != null ? model.getObject().getAuthor().getName() + " on "
                + model.getObject().getCreatedFormatted() : null;
    }

}
