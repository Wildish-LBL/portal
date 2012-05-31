package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;

/**
 * A resource group information panel.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class InfoPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            resource group model
     */
    public InfoPanel(String id, CompoundPropertyModel<ResourceGroup> model) {
        super(id, model);
        setOutputMarkupId(true);
        add(new Label("title"));
        add(new Label("description"));
    }

}
