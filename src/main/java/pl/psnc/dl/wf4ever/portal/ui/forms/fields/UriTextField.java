package pl.psnc.dl.wf4ever.portal.ui.forms.fields;

import java.net.URI;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.convert.IConverter;

import pl.psnc.dl.wf4ever.portal.pages.util.URIConverter;

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
