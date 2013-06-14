package pl.psnc.dl.wf4ever.portal.components.form;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 * URI converter that validates URIs.
 * 
 * @author piotrekhol
 * 
 */
public class URIConverter implements IConverter<URI> {

    /** id. */
    private static final long serialVersionUID = -309855395859299167L;


    @Override
    public URI convertToObject(String value, Locale locale) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new ConversionException("'" + value + "' is not a valid URI");
        }
    }


    @Override
    public String convertToString(URI value, Locale locale) {
        return value.toString();
    }
}
