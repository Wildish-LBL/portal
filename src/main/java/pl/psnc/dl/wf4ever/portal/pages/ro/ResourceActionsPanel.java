package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.components.form.ProtectedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.ProtectedEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeleteClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMoveClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.UpdateClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.CommentAddClickedEvent;

/**
 * Panel with action buttons for a selected resource.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ResourceActionsPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ResourceActionsPanel.class);

    /** Form for the buttons. */
    private Form<Void> form;

    /** Button for updating resources. */
    private ProtectedAjaxEventButton updateButton;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            selected resource model
     */
    public ResourceActionsPanel(String id, final IModel<Resource> model) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);

        form = new Form<Void>("form");
        add(form);
        form.add(new ExternalLink("download", new PropertyModel<String>(model, "uri")));
        updateButton = new ProtectedAjaxEventButton("update", form, null, UpdateClickedEvent.class);
        form.add(updateButton);
        form.add(new ProtectedAjaxEventButton("delete", form, null, ResourceDeleteClickedEvent.class));
        form.add(new ProtectedAjaxEventButton("move", form, null, ResourceMoveClickedEvent.class));
        form.add(new ProtectedEditAjaxEventButton("add-comment", form, model, null, CommentAddClickedEvent.class));
        form.add(new Button("show-all"));
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        setEnabled(getDefaultModelObject() != null);
        updateButton.setEnabled(!(getDefaultModelObject() instanceof Folder));
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof ResourceSelectedEvent) {
            onResourceSelected((ResourceSelectedEvent) event.getPayload());
        }
    }


    /**
     * Refresh this panel when the selected resource changes.
     * 
     * @param event
     *            AJAX event
     */
    private void onResourceSelected(ResourceSelectedEvent event) {
        event.getTarget().add(this);
    }

}
