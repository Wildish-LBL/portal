package pl.psnc.dl.wf4ever.portal.components.form;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * A label that displays "Not set" when the value is null.
 * 
 * @author piotrekhol
 * 
 */
public class NotSetLabel extends Label {

    /** id. */
    private static final long serialVersionUID = 5810881670929377406L;

    /** The model for the value, can have a null value. */
    private IModel<String> originalModel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the model for the value, can have a null value
     */
    public NotSetLabel(String id, IModel<String> model) {
        super(id);
        setDefaultModel(new PropertyModel<String>(this, "valueOrNotSet"));
        originalModel = model;
    }


    public IModel<String> getOriginalModel() {
        return originalModel;
    }


    public void setOriginalModel(IModel<String> originalModel) {
        this.originalModel = originalModel;
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setEscapeModelStrings(originalModel.getObject() != null);
    }


    /**
     * If the original value is not null, return it, otherwise return "<em>Not set</em>".
     * 
     * @return the original value or a replacement text
     */
    public String getValueOrNotSet() {
        if (originalModel.getObject() != null) {
            return originalModel.getObject();
        } else {
            return "<em>Not set</em>";
        }
    }

}
