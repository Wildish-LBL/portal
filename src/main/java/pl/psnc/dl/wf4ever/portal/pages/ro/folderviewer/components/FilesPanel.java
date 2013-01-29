package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.purl.wf4ever.rosrs.client.Thing;

import pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components.behaviours.IAjaxLinkListener;

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
    /** Selected file. */
    private ListItem<Thing> selectedItem;
    /** List model. */
    private IModel<List<Thing>> model;
    /** Listeners. */
    private List<IAjaxLinkListener> listeners;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public FilesPanel(String id, IModel<List<Thing>> model) {
        super(id, model);
        setOutputMarkupId(true);
        this.selectedItem = null;
        this.model = model;
        listeners = new ArrayList<IAjaxLinkListener>();
        listView = new PropertyListView<Thing>("filesListView", model) {

            private static final long serialVersionUID = -6310254217773728128L;


            @Override
            protected void populateItem(final ListItem<Thing> item) {
                Label label = new Label("tailLabel", item.getModelObject().calculateName());
                AjaxLink link = new AjaxLink("tailLink") {

                    /** Serialization */
                    private static final long serialVersionUID = 6984606437114241889L;


                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        for (IAjaxLinkListener listener : listeners) {
                            selectedItem = item;
                            listener.onAjaxLinkOnClick(target);
                        }
                    }
                };

                link.add(AttributeModifier.replace("uri", item.getModelObject().getUri()));
                item.add(link);
                link.add(label);
            }

        };
        add(listView);
    }


    /**
     * Add new link listener.
     * 
     * @param listener
     *            link listener
     */
    public void addLinkListeners(IAjaxLinkListener listener) {
        listeners.add(listener);
    }


    /**
     * Get selected item.
     * 
     * @return the currently seleted file
     */
    public Thing getSelectedItem() {
        if (selectedItem != null) {
            return selectedItem.getModel().getObject();
        } else {
            return null;
        }
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(cssResourceReference);
        super.renderHead(response);
    }
}