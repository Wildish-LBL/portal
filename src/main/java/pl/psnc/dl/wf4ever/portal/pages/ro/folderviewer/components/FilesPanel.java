package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components;

import java.net.URI;
import java.util.ArrayList;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.Thing;

/**
 * List of files displayed as tails.
 * 
 * @author pejot
 * 
 */
public class FilesPanel extends Panel {

    /** List of objects. */
    ArrayList<Thing> thingsList;
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
    public FilesPanel(String id) {
        super(id);
        //temporary object factory
        thingsList = new ArrayList<Thing>();
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/1/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/2/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/3/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/4/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/5/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/6/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/7/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/8/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/8/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/9/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/10/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/11/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        thingsList.add(new Thing(URI.create("http://www.example.com/thing/12/"), URI
                .create("http://www.example.com/thing/creator/1"), DateTime.now()));
        listView = new PropertyListView<Thing>("filesListView", thingsList) {

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