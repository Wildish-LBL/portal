package pl.psnc.dl.wf4ever.portal.pages.ro.components;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Statement;

/**
 * A utility class for creating links to resources inside the RO.
 * 
 * @author piotrekhol
 * 
 */
@SuppressWarnings("serial")
public class InternalLinkFragment extends Fragment {

    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param markupId
     *            fragment wicket id
     * @param markupProvider
     *            which component defines the fragment
     * @param statement
     *            the statement for which the link is created
     * @param roPage
     *            TODO
     */
    public InternalLinkFragment(String id, String markupId, MarkupContainer markupProvider, Statement statement,
            ResearchObject researchObject) {
        super(id, markupId, markupProvider);
        String internalName = "./" + researchObject.getUri().relativize(statement.getSubjectURI()).toString();
        add(new AjaxLink<String>("link", new Model<String>(internalName)) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                // TODO Auto-generated method stub

            }
        }.add(new Label("name", internalName.toString())));
    }
}