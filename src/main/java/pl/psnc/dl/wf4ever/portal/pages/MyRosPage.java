package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.ros.RoCreateClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoCreateReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoDeleteClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.RoDeleteReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.ZipAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.ros.ZipAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.modals.CreateROModal;
import pl.psnc.dl.wf4ever.portal.modals.DeleteROModal;
import pl.psnc.dl.wf4ever.portal.modals.UploadZipModal;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.utils.ModelIteratorAdapter;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sun.jersey.api.client.Client;

/**
 * A page with user's own Research Objects.
 * 
 * @author piotrekhol
 * 
 */
@AuthorizeInstantiation("USER")
public class MyRosPage extends BasePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(MyRosPage.class);

    /** ROs selected by the user. */
    final List<ResearchObject> selectedResearchObjects = new ArrayList<ResearchObject>();

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** Event bus. */
    private IModel<EventBus> eventBusModel;

    /** List of ROs. */
    private ArrayList<ResearchObject> researchObjects;

    /** Form with the list and buttons. */
    private Form<?> form;


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws URISyntaxException
     *             can't connect to RODL
     * @throws ROSRSException
     *             getting the RO list ends with an unexpected response code
     */
    public MyRosPage(final PageParameters parameters)
            throws URISyntaxException, ROSRSException {
        super(parameters);

        MySession session = MySession.get();
        final ROSRService rosrs = session.getRosrs();
        List<URI> uris = rosrs.getROList(false);
        researchObjects = new ArrayList<ResearchObject>();
        for (URI uri : uris) {
            try {
                researchObjects.add(new ResearchObject(uri, rosrs));
            } catch (Exception e) {
                error("Could not get manifest for: " + uri + " (" + e.getMessage() + ")");
            }
        }

        eventBusModel = session.addEventBus();
        eventBusModel.getObject().register(this);

        form = new Form<Void>("form");
        form.setOutputMarkupId(true);
        add(form);
        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        form.add(feedbackPanel);
        CheckGroup<ResearchObject> group = new CheckGroup<ResearchObject>("group", selectedResearchObjects);
        form.add(group);
        RefreshingView<ResearchObject> list = new MyROsRefreshingView("rosListView", researchObjects);
        group.add(list);

        form.add(new AuthenticatedAjaxEventButton("delete", form, eventBusModel, RoDeleteClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("add", form, eventBusModel, RoCreateClickedEvent.class));
        form.add(new AuthenticatedAjaxEventButton("add-zip", form, eventBusModel, ZipAddClickedEvent.class));
        form.add(new BookmarkablePageLink<Void>("myExpImport", MyExpImportPage.class));

        add(new DeleteROModal("delete-ro-modal", eventBusModel, new PropertyModel<List<ResearchObject>>(this,
                "selectedResearchObjects")));
        add(new CreateROModal("create-ro-modal", eventBusModel));
        add(new UploadZipModal("zip-upload-modal", eventBusModel));
    }


    /**
     * Create a new RO.
     * 
     * @param event
     *            event
     */
    @Subscribe
    public void onRoCreate(RoCreateReadyEvent event) {
        try {
            ResearchObject ro;
            if (event.getTemplate() == null) {
                ro = ResearchObject.create(MySession.get().getRosrs(), event.getRoId());
            } else {
                ro = event.getTemplate().create(MySession.get().getRosrs(), event.getRoId());
            }
            researchObjects.add(ro);
        } catch (ROSRSException e) {
            if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                error("This ID is already used.");
            } else {
                error("Could not add Research Object: " + event.getRoId() + " (" + e.getMessage() + ")");
            }
            LOG.error("Could not create RO", e);
        } catch (ROException e) {
            error("Could not add Research Object: " + event.getRoId() + " (" + e.getMessage() + ")");
            LOG.error("Could not create RO", e);
        }
        event.getTarget().add(form);
    }


    /**
     * Delete an RO.
     * 
     * @param event
     *            event
     */
    @Subscribe
    public void onRoDelete(RoDeleteReadyEvent event) {
        for (ResearchObject ro : selectedResearchObjects) {
            try {
                ro.delete();
                researchObjects.remove(ro);
            } catch (Exception e) {
                error("Could not delete Research Object: " + ro.getUri() + " (" + e.getMessage() + ")");
            }
        }
        event.getTarget().add(form);
    }


    /**
     * Process the details of the ZIP archive and redirect to the RO creation page.
     * 
     * @param event
     *            event with URI or path of the ZIP archive
     */
    @Subscribe
    public void onRoFromZip(ZipAddReadyEvent event) {
        try {
            InputStream inputStream;
            String name;
            if (event.getUploadedFile() != null) {
                inputStream = event.getUploadedFile().getInputStream();
                name = event.getUploadedFile().getClientFileName();
            } else {
                inputStream = Client.create().resource(event.getResourceUri()).get(InputStream.class);
                name = Paths.get(event.getResourceUri()).getFileName().toString();
            }
            setResponsePage(new CreateROFromZipPage(inputStream, name));
        } catch (IOException e) {
            LOG.error("Invalid ZIP archive", e);
            error("Invalid ZIP archive: " + e.getLocalizedMessage());
        }
    }


    /**
     * The ROs refreshing view.
     * 
     * @author piotrekhol
     * 
     */
    private final class MyROsRefreshingView extends RefreshingView<ResearchObject> {

        /** id. */
        private static final long serialVersionUID = -6310254217773728128L;

        /** ROs. */
        private final List<ResearchObject> researchObjects;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param researchObjects
         *            list of ROs
         */
        private MyROsRefreshingView(String id, List<ResearchObject> researchObjects) {
            super(id);
            this.researchObjects = researchObjects;
        }


        @Override
        protected void populateItem(Item<ResearchObject> item) {
            ResearchObject researchObject = (ResearchObject) item.getDefaultModelObject();
            item.add(new Check<ResearchObject>("checkbox", item.getModel()));
            BookmarkablePageLink<Void> link = new BookmarkablePageLink<>("link", RoPage.class);
            link.getPageParameters().add("ro", researchObject.getUri().toString());
            link.add(new Label("uri"));
            item.add(link);
            item.add(new Label("createdFormatted"));
        }


        @Override
        protected Iterator<IModel<ResearchObject>> getItemModels() {
            return new ModelIteratorAdapter<ResearchObject>(researchObjects.iterator()) {

                @Override
                protected IModel<ResearchObject> model(ResearchObject ro) {
                    return new CompoundPropertyModel<ResearchObject>(ro);
                }
            };
        }
    }
}
