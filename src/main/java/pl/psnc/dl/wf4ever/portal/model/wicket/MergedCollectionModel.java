package pl.psnc.dl.wf4ever.portal.model.wicket;

import java.util.Collection;

import org.apache.wicket.model.ChainingModel;

/**
 * A label that displays all values of a collection separated by commas.
 * 
 * @author piotrekhol
 * 
 */
public class MergedCollectionModel extends ChainingModel<String> {

    /** id. */
    private static final long serialVersionUID = 5810881670929377406L;


    /**
     * Constructor.
     * 
     * @param modelObject
     *            the model for the value, can have a null value, or the value
     */
    public MergedCollectionModel(Object modelObject) {
        super(modelObject);
    }


    /**
     * If the value is a collection, return all values joined with ", ". If the collection is empty, return null. If the
     * value is not a collection, return it as is.
     * 
     * @return merged values or null
     */
    @Override
    public String getObject() {
        Object object = super.getObject();
        if (object != null) {
            if (object instanceof Collection) {
                Collection<?> collection = (Collection<?>) object;
                StringBuilder sb = new StringBuilder();
                for (Object object2 : collection) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(object2.toString());
                }
                return sb.length() == 0 ? null : sb.toString();
            } else {
                return object.toString();
            }
        }
        return null;
    }

}
