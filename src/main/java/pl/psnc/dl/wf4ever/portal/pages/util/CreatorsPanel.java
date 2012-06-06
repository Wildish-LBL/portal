package pl.psnc.dl.wf4ever.portal.pages.util;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.model.Creator;

/**
 * A set of {@link CreatorPanel} separated by commas.
 * 
 * @author piotrekhol
 * 
 */
public class CreatorsPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -2077084041006536701L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            a model of a list of {@link Creator}
     */
    @SuppressWarnings("serial")
    public CreatorsPanel(String id, IModel<List<Creator>> model) {
        super(id, model);
        ListView<Creator> list = new ListView<Creator>("creators", model) {

            @Override
            protected void populateItem(ListItem<Creator> item) {
                item.add(new CreatorPanel("creator", item.getModel()));
                item.add(new WebMarkupContainer("separator").setVisible(item.getIndex() > 0));
            }

        };
        add(list);
    }
}
