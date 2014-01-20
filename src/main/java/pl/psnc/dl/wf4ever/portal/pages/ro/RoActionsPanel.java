package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.evo.EvoType;

import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.form.ProtectedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.MetadataDownloadClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.ReleaseCreateEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.evo.SnapshotCreateEvent;

/**
 * A panel for actions related to the RO.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class RoActionsPanel extends Panel {

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
     */
    public RoActionsPanel(String id, final IModel<ResearchObject> model) {
        super(id, model);
        setOutputMarkupId(true);
        form = new Form<Void>("form");
        add(form);
        form.add(new ExternalLink("downloadROZipped", new PropertyModel<String>(this, "ROZipLink")));
        form.add(new AjaxEventButton("downloadMetadata", form, null, MetadataDownloadClickedEvent.class));
        snapshotButton = new ProtectedAjaxEventButton("snapshot-ro-button", form, this, SnapshotClickedEvent.class);
        form.add(snapshotButton);
        releaseButton = new ProtectedAjaxEventButton("release-ro-button", form, this, ReleaseClickedEvent.class);
        form.add(releaseButton);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof SnapshotClickedEvent) {
            onSnapshotClicked((SnapshotClickedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof ReleaseClickedEvent) {
            onReleaseClicked((ReleaseClickedEvent) event.getPayload());
        }
    }


    /**
     * Start making a snapshot.
     * 
     * @param event
     *            AJAX event
     */
    private void onSnapshotClicked(SnapshotClickedEvent event) {
        send(getPage(), Broadcast.BREADTH, new SnapshotCreateEvent(event.getTarget()));
    }


    /**
     * Start making a release.
     * 
     * @param event
     *            AJAX event
     */
    private void onReleaseClicked(ReleaseClickedEvent event) {
        send(getPage(), Broadcast.BREADTH, new ReleaseCreateEvent(event.getTarget()));
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
