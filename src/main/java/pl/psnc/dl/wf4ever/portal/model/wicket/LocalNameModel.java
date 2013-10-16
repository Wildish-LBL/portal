package pl.psnc.dl.wf4ever.portal.model.wicket;

import java.net.URI;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

/**
 * This model returns a local name given a URI. This is either a fragment ID or the last path segment.
 * 
 * @author piotrekhol
 * 
 */
public class LocalNameModel extends AbstractReadOnlyModel<String> {

    /** id. */
    private static final long serialVersionUID = -5110228026110644007L;

    /** URI model. */
    private IModel<URI> model;

    private String notUri;


    /**
     * Constructor.
     * 
     * @param model
     *            URI model
     */
    public LocalNameModel(IModel<URI> model) {
        this.model = model;
    }


    public LocalNameModel(PropertyModel<String> propertyModel) {
        try {
            this.model = new PropertyModel<URI>(URI.create(propertyModel.getObject()), null);
        } catch (IllegalArgumentException e) {
            model = null;
            notUri = propertyModel.getObject();
        }
    }


    @Override
    public String getObject() {
        if (model != null) {
            return model.getObject() != null ? localName(model.getObject()) : null;
        } else {
            return notUri;
        }
    }


    /**
     * Returns fragment or last path segment of a URI.
     * 
     * @param uri
     *            the URI to parse
     * @return fragment or last segment
     */
    private String localName(URI uri) {
        if (model == null) {
            return notUri;
        }

        if (uri.getFragment() != null && !uri.getFragment().isEmpty()) {
            return uri.getFragment();
        }

        if (uri.getPath().endsWith("/")) {
            String[] tmparray = uri.getPath().split("/");
            if (tmparray.length == 0) {
                return "/";
            } else {
                return tmparray[tmparray.length - 1];
            }

        }
        String result = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
        if (result.equals("")) {
            return uri.toString();
        }
        return result;
    }
}
