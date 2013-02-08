package pl.psnc.dl.wf4ever.portal.pages.ro.components;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.purl.wf4ever.rosrs.client.Statement;

/**
 * A utility class for creating an external link to a property of a statement.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
public class ExternalLinkFragment extends Fragment {

    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param markupId
     *            fragment wicket id
     * @param markupProvider
     *            which component defines the fragment
     * @param model
     *            statement model
     * @param property
     *            property of a statement
     */
    public ExternalLinkFragment(String id, String markupId, MarkupContainer markupProvider,
            CompoundPropertyModel<Statement> model, String property) {
        super(id, markupId, markupProvider, model);
        add(new ExternalLink("link", model.<String> bind(property), model.<String> bind(property)));
    }
}