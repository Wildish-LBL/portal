package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.AddLinkEvent;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;
import pl.psnc.dl.wf4ever.portal.events.ShowAllAnnotationsEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.DuplicateEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.MoveEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.UpdateClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeleteClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AnnotateEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.CommentAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.ImportAnnotationClickedEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

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


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            selected resource model
     * @param eventBusModel
     *            event bus model for the button clicks
     */
    public ResourceActionsPanel(String id, final IModel<Resource> model, final IModel<EventBus> eventBusModel) {
        super(id);
        eventBusModel.getObject().register(this);

        setOutputMarkupId(true);

        form = new Form<Void>("form");
        add(form);
        form.add(new ExternalLink("download", new PropertyModel<String>(model, "uri")));
        form.add(new AuthenticatedAjaxEventButton("update", form, eventBusModel, UpdateClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("delete", form, eventBusModel, ResourceDeleteClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("move", form, eventBusModel, MoveEvent.class));
        form.add(new AuthenticatedAjaxEventButton("duplicate", form, eventBusModel, DuplicateEvent.class));
        form.add(new AuthenticatedAjaxEventButton("link-add", form, eventBusModel, AddLinkEvent.class));
        form.add(new AnnotationEditAjaxEventButton("add-comment", form, model, eventBusModel,
                CommentAddClickedEvent.class));
        form.add(new AnnotationEditAjaxEventButton("import-annotations", form, model, eventBusModel,
                ImportAnnotationClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("annotate", form, eventBusModel, AnnotateEvent.class));
        form.add(new AjaxEventButton("show-all", form, eventBusModel, ShowAllAnnotationsEvent.class));
    }


    /**
     * Refresh this panel when the selected resource changes.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onResourceSelected(ResourceSelectedEvent event) {
        event.getTarget().add(this);
    }

}
