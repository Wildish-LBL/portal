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
        this.model = new PropertyModel<URI>(URI.create(propertyModel.getObject()), null);
    }


    @Override
    public String getObject() {
        return model.getObject() != null ? localName(model.getObject()) : null;
    }


    /**
     * Returns fragment or last path segment of a URI.
     * 
     * @param uri
     *            the URI to parse
     * @return fragment or last segment
     */
    private String localName(URI uri) {
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
        return uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
    }
}
