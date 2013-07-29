package pl.psnc.dl.wf4ever.portal.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 * A label that displays "Not set" when the value is null.
 * 
 * @author piotrekhol
 * 
 */
public class NotSetModel extends AbstractReadOnlyModel<String> {

    /** id. */
    private static final long serialVersionUID = 5810881670929377406L;

    /** The model for the value, can have a null value. */
    private IModel<String> originalModel;


    /**
     * Constructor.
     * 
     * @param model
     *            the model for the value, can have a null value
     */
    public NotSetModel(IModel<String> model) {
        originalModel = model;
    }


    public IModel<String> getOriginalModel() {
        return originalModel;
    }


    /**
     * If the original value is not null, return it, otherwise return "<em>Not set</em>".
     * 
     * @return the original value or a replacement text
     */
    @Override
    public String getObject() {
        if (originalModel.getObject() != null) {
            return originalModel.getObject();
        } else {
            return "<em>Not set</em>";
        }
    }

}
