package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.purl.wf4ever.rosrs.client.Thing;

/**
 * List of files displayed as tails.
 * 
 * @author pejot
 * 
 */
public class FilesPanel extends Panel {

    /** List of files. */
    private ListView<Thing> listView;
    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** CSS tail resource reference. */
    private CssResourceReference cssResourceReference = new CssResourceReference(FilesPanel.class, "tails.css");


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public FilesPanel(String id, IModel<List<Thing>> model) {
        super(id, model);
        setOutputMarkupId(true);
        listView = new PropertyListView<Thing>("filesListView", model) {

            private static final long serialVersionUID = -6310254217773728128L;


            @Override
            protected void populateItem(ListItem<Thing> item) {
                Label label = new Label("tailLabel", item.getModelObject().calculateName());
                label.add(AttributeModifier.replace("uri", item.getModelObject().getUri()));
                item.add(label);
            }

        };
        add(listView);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(cssResourceReference);
        super.renderHead(response);
    }
}