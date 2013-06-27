package pl.psnc.dl.wf4ever.portal.components.annotations;

import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Annotation;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.edit.DeleteEvent;
import pl.psnc.dl.wf4ever.portal.events.edit.EditEvent;
import pl.psnc.dl.wf4ever.portal.model.AnnotationTimestampModel;
import pl.psnc.dl.wf4ever.portal.model.LocalNameModel;

import com.google.common.eventbus.EventBus;

/**
 * A panel for inline comments, show the annotation author and creation date.
 * 
 * @author piotrekhol
 * 
 */
public class EditableAnnotationTextPanel extends EventPanel {

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(EditableAnnotationTextPanel.class);

    /** id. */
    private static final long serialVersionUID = 1L;

    /** The read only view of the field. */
    private Fragment viewFragment;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the model for the quad
     * @param eventBusModel
     *            event bus model for when a comment is added, deleted or edited
     */
    public EditableAnnotationTextPanel(String id, IModel<AnnotationTriple> model, final IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupPlaceholderTag(true);
        LoadableDetachableModel<EventBus> internalEventBusModel = new LoadableDetachableModel<EventBus>() {

            /** id. */
            private static final long serialVersionUID = 5225667860067218852L;


            @Override
            protected EventBus load() {
                return new EventBus();
            }
        };
        internalEventBusModel.getObject().register(this);
        viewFragment = newViewFragment(model, internalEventBusModel);
        add(viewFragment);
    }


    /**
     * Create new fragment for the read-only view.
     * 
     * @param model
     *            value model
     * @param internalEventBusModel
     *            event bus for button clicks
     * @return a fragment
     */
    protected Fragment newViewFragment(IModel<AnnotationTriple> model, IModel<EventBus> internalEventBusModel) {
        return new ViewFragment("content", "view", this, model, internalEventBusModel);
    }


    /**
     * Read-only view fragment.
     * 
     * @author piotrekhol
     * 
     */
    protected class ViewFragment extends Fragment {

        /** id. */
        private static final long serialVersionUID = -4169842101720666349L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param markupId
         *            fragment wicket id
         * @param markupProvider
         *            container defining the fragment
         * @param model
         *            value model
         * @param internalEventBusModel
         *            event bus model for button clicks
         */
        public ViewFragment(String id, String markupId, MarkupContainer markupProvider, IModel<AnnotationTriple> model,
                final IModel<EventBus> internalEventBusModel) {
            super(id, markupId, markupProvider, model);
            setOutputMarkupPlaceholderTag(true);

            WebMarkupContainer propColumn = new WebMarkupContainer("property");
            propColumn.add(AttributeAppender.replace("data-original-title", new PropertyModel<>(model, "property")));
            propColumn.add(new Label("property-name", new LocalNameModel(new PropertyModel<URI>(model, "property"))));
            add(propColumn);

            WebMarkupContainer valueColumn = new WebMarkupContainer("value");
            valueColumn.add(AttributeAppender.replace("data-original-title", new AnnotationTimestampModel(
                    new PropertyModel<Annotation>(model, "annotation"))));
            valueColumn.add(new Label("value", new PropertyModel<String>(model, "value")));
            valueColumn.add(new AuthenticatedAjaxEventButton("edit", null, internalEventBusModel, EditEvent.class));
            valueColumn.add(new AuthenticatedAjaxEventButton("delete", null, internalEventBusModel, DeleteEvent.class));
            add(valueColumn);
        }

    }

}
