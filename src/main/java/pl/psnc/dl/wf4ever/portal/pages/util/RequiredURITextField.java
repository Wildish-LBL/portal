package pl.psnc.dl.wf4ever.portal.pages.util;

import java.net.URI;

import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

/**
 * A required text field that validates a correct URI.
 * 
 * @author piotrekhol
 * 
 */
public final class RequiredURITextField extends RequiredTextField<URI> {

    /** id. */
    private static final long serialVersionUID = -1735597253795967379L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            URI model
     */
    public RequiredURITextField(String id, IModel<URI> model) {
        super(id, model, URI.class);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <C> IConverter<C> getConverter(Class<C> type) {
        return (IConverter<C>) new URIConverter();
    }
}
