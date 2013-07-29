package pl.psnc.dl.wf4ever.portal.model;

import org.apache.wicket.model.ChainingModel;

/**
 * A label that displays "Not set" when the value is null.
 * 
 * @author piotrekhol
 * 
 */
public class NotSetModel extends ChainingModel<String> {

    /** id. */
    private static final long serialVersionUID = 5810881670929377406L;


    /**
     * Constructor.
     * 
     * @param modelObject
     *            the model for the value, can have a null value, or the value
     */
    public NotSetModel(Object modelObject) {
        super(modelObject);
    }


    /**
     * If the original value is not null, return it, otherwise return "<em>Not set</em>".
     * 
     * @return the original value or a replacement text
     */
    @Override
    public String getObject() {
        if (super.getObject() != null) {
            return super.getObject();
        } else {
            return "<em>Not set</em>";
        }
    }

}
