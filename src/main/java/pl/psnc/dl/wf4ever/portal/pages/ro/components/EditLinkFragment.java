package pl.psnc.dl.wf4ever.portal.pages.ro.components;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.panel.Fragment;

/**
 * A utility class for creating a link for editing a statement.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
public class EditLinkFragment extends Fragment {

    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param markupId
     *            fragment wicket id
     * @param markupProvider
     *            which component defines the fragment
     * @param link
     *            link defining the action upon click
     */
    public EditLinkFragment(String id, String markupId, MarkupContainer markupProvider, AjaxFallbackLink<String> link) {
        super(id, markupId, markupProvider);
        add(link);
    }
}