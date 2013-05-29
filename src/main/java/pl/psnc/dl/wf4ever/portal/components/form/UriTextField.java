package pl.psnc.dl.wf4ever.portal.components.form;

import java.net.URI;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.convert.IConverter;


/**
 * Universal Uri html input.
 * 
 * @author pejot
 * 
 */
public class UriTextField extends TextField<URI> {

    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public UriTextField(String id) {
        super(id);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return (IConverter<C>) new URIConverter();
    }

}
