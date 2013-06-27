package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.evo.EvoType;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.MetadataDownloadClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseCreateEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotCreateEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A panel for actions related to the RO.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class RoActionsPanel extends EventPanel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RoActionsPanel.class);

    /** The form aggregating the buttons. */
    private Form<Void> form;

    /** Make snapshot button. */
    private AjaxEventButton snapshotButton;

    /** Make release button. */
    private AjaxEventButton releaseButton;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            the model of the research object
     * @param eventBusModel
     *            event bus model for button clicks
     */
    public RoActionsPanel(String id, final IModel<ResearchObject> model, final IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupId(true);
        form = new Form<Void>("form");
        add(form);
        form.add(new ExternalLink("downloadROZipped", new PropertyModel<String>(this, "ROZipLink")));
        form.add(new AjaxEventButton("downloadMetadata", form, eventBusModel, MetadataDownloadClickedEvent.class));
        snapshotButton = new AuthenticatedAjaxEventButton("snapshot-ro-button", form, eventBusModel,
                SnapshotClickedEvent.class);
        form.add(snapshotButton);
        releaseButton = new AuthenticatedAjaxEventButton("release-ro-button", form, eventBusModel,
                ReleaseClickedEvent.class);
        form.add(releaseButton);
    }


    /**
     * Start making a snapshot.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onSnapshotClicked(SnapshotClickedEvent event) {
        eventBusModel.getObject().post(new SnapshotCreateEvent(event.getTarget()));
    }


    /**
     * Start making a release.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onReleaseClicked(ReleaseClickedEvent event) {
        eventBusModel.getObject().post(new ReleaseCreateEvent(event.getTarget()));
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        ResearchObject researchObject = (ResearchObject) getDefaultModelObject();
        snapshotButton.setEnabled(researchObject.getEvoType() == EvoType.LIVE);
        releaseButton.setEnabled(researchObject.getEvoType() == EvoType.LIVE
                || researchObject.getEvoType() == EvoType.SNAPSHOT);
    }


    /**
     * Get link to zipped RO.
     * 
     * @return a link to zipped RO
     */
    public String getROZipLink() {
        return ((ResearchObject) getDefaultModelObject()).getUri().toString().replaceFirst("/ROs/", "/zippedROs/");
    }

}
