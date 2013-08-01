package pl.psnc.dl.wf4ever.portal.components.form;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.purl.wf4ever.rosrs.client.Utils;

/**
 * URI converter that validates absolute URIs.
 * 
 * @author piotrekhol
 * 
 */
public class AbsoluteURIConverter implements IConverter<URI> {

    /** id. */
    private static final long serialVersionUID = -309855395859299167L;


    @Override
    public URI convertToObject(String value, Locale locale) {
        if (Utils.isAbsoluteURI(value)) {
            try {
                return new URI(value);
            } catch (URISyntaxException e) {
                throw new ConversionException("'" + value + "' is not a valid URI");
            }
        } else {
            ConversionException ex = new ConversionException("'" + value + "' is not a valid absolute URI");
            ex.setVariable("value", value);
            throw ex;
        }
    }


    @Override
    public String convertToString(URI value, Locale locale) {
        return value.toString();
    }

}
