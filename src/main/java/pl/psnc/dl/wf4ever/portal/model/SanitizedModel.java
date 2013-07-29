package pl.psnc.dl.wf4ever.portal.model;

import org.apache.wicket.model.ChainingModel;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * A model that will escape all HTML tags that are not in the white list.
 * 
 * @author piotrekhol
 * 
 */
public class SanitizedModel extends ChainingModel<String> {

    /** id. */
    private static final long serialVersionUID = 5810881670929377406L;

    /** A set of policies for allowed HTML tags. */
    private static PolicyFactory sanitizer = Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS)
            .and(Sanitizers.STYLES).and(new HtmlPolicyBuilder().allowElements("em").toFactory());


    /**
     * Constructor.
     * 
     * @param modelObject
     *            the model for the value, can have a null value, or the value
     */
    public SanitizedModel(Object modelObject) {
        super(modelObject);
    }


    /**
     * If the original value is not null, return it, otherwise return "<em>Not set</em>".
     * 
     * @return the original value or a replacement text
     */
    @Override
    public String getObject() {
        String object = super.getObject();
        if (object == null) {
            return null;
        }
        return sanitizer.sanitize(object);
    }

}
