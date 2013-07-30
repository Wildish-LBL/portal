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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
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
import pl.psnc.dl.wf4ever.portal.components.form.AuthenticatedAjaxDecoratedButton;
import pl.psnc.dl.wf4ever.portal.modals.CreateROModal;
import pl.psnc.dl.wf4ever.portal.modals.DeleteROModal;
import pl.psnc.dl.wf4ever.portal.modals.UploadZipModal;
import pl.psnc.dl.wf4ever.portal.model.template.ResearchObjectTemplate;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.utils.ModelIteratorAdapter;

import com.google.common.eventbus.EventBus;
import com.hp.hpl.jena.vocabulary.DCTerms;
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
    private List<ResearchObject> selectedResearchObjects = new ArrayList<ResearchObject>();

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** Event bus. */
    private IModel<EventBus> eventBusModel;

    /** List of ROs. */
    private List<ResearchObject> researchObjects;

    /** Form with the list and buttons. */
    private Form<?> form;

    /** Modal window for deleting the ROs. */
    private DeleteROModal deleteROModal;

    /** Modal window for creating a new RO. */
    private CreateROModal createROModal;

    /** Modal window for creating an RO from a ZIP. */
    private UploadZipModal uploadZipModal;


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
        CheckGroup<ResearchObject> group = new CheckGroup<ResearchObject>("group",
                new PropertyModel<List<ResearchObject>>(this, "selectedResearchObjects"));
        form.add(group);
        RefreshingView<ResearchObject> list = new MyROsRefreshingView("rosListView", researchObjects);
        group.add(list);

        form.add(new ShowDeleteModelButton("delete", form));
        form.add(new ShowCreateROModelButton("add", form));
        form.add(new ShowUploadZIPModelButton("add-zip", form));
        form.add(new BookmarkablePageLink<Void>("myExpImport", MyExpImportPage.class));

        deleteROModal = new InternalDeleteROModal("delete-ro-modal", eventBusModel,
                new PropertyModel<List<ResearchObject>>(this, "selectedResearchObjects"));
        add(deleteROModal);
        createROModal = new InternalCreateROModal("create-ro-modal", eventBusModel);
        add(createROModal);
        uploadZipModal = new InternalUploadZipModal("zip-upload-modal", eventBusModel);
        add(uploadZipModal);
    }


    /**
     * Instance that.
     * 
     * @author piotrekhol
     * 
     */
    private final class InternalUploadZipModal extends UploadZipModal {

        /** id. */
        private static final long serialVersionUID = 8322434091972051487L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param eventBusModel
         *            event bus
         */
        private InternalUploadZipModal(String id, IModel<EventBus> eventBusModel) {
            super(id, eventBusModel);
        }


        @Override
        protected void onApply(AjaxRequestTarget target, FileUpload uploadedFile) {
            try {
                setResponsePage(new CreateROFromZipPage(uploadedFile.getInputStream(), uploadedFile.getClientFileName()));
            } catch (IOException e) {
                LOG.error("Invalid ZIP archive", e);
                error("Invalid ZIP archive: " + e.getLocalizedMessage());
            }
        }


        @Override
        protected void onApply(AjaxRequestTarget target, URI resourceURI) {
            setResponsePage(new CreateROFromZipPage(Client.create().resource(resourceURI).get(InputStream.class), Paths
                    .get(resourceURI).getFileName().toString()));
        }
    }


    /**
     * Instance that deletes the ROs.
     * 
     * @author piotrekhol
     * 
     */
    private final class InternalDeleteROModal extends DeleteROModal {

        /** id. */
        private static final long serialVersionUID = -535690076411811855L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param eventBusModel
         *            event bus
         * @param toDelete
         *            ROs to delete
         */
        private InternalDeleteROModal(String id, IModel<EventBus> eventBusModel,
                IModel<? extends List<ResearchObject>> toDelete) {
            super(id, eventBusModel, toDelete);
        }


        @Override
        public void onApply(AjaxRequestTarget target) {
            for (ResearchObject ro : selectedResearchObjects) {
                try {
                    ro.delete();
                    researchObjects.remove(ro);
                } catch (Exception e) {
                    error("Could not delete Research Object: " + ro.getUri() + " (" + e.getMessage() + ")");
                }
            }
            target.add(MyRosPage.this.form);
        }
    }


    /**
     * Instance that creates the RO.
     * 
     * @author piotrekhol
     * 
     */
    private final class InternalCreateROModal extends CreateROModal {

        /** id. */
        private static final long serialVersionUID = 1218611500046636655L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param eventBusModel
         *            event bus
         */
        private InternalCreateROModal(String id, IModel<EventBus> eventBusModel) {
            super(id, eventBusModel);
        }


        @Override
        public void onApply(AjaxRequestTarget target, String roId, ResearchObjectTemplate template, String title,
                String description) {
            try {
                ResearchObject ro;
                if (template == null) {
                    ro = ResearchObject.create(MySession.get().getRosrs(), roId);
                } else {
                    ro = template.create(MySession.get().getRosrs(), roId);
                }
                researchObjects.add(ro);
                if (title != null) {
                    ro.createPropertyValue(DCTerms.title, title);
                }
                if (description != null) {
                    ro.createPropertyValue(DCTerms.description, description);
                }
            } catch (ROSRSException e) {
                if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                    error("This ID is already used.");
                } else {
                    error("Could not add Research Object: " + roId + " (" + e.getMessage() + ")");
                }
                LOG.error("Could not create RO", e);
            } catch (ROException e) {
                error("Could not add Research Object: " + roId + " (" + e.getMessage() + ")");
                LOG.error("Could not create RO", e);
            }
            target.add(MyRosPage.this.form);
        }
    }


    /**
     * A button.
     * 
     * @author piotrekhol
     * 
     */
    private class ShowDeleteModelButton extends AuthenticatedAjaxDecoratedButton {

        /** id. */
        private static final long serialVersionUID = -4510072124035188464L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket ID
         * @param form
         *            for which will be validated
         */
        public ShowDeleteModelButton(String id, Form<?> form) {
            super(id, form);
        }


        @Override
        protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onAfterSubmit(target, form);
            IModel<ArrayList<ResearchObject>> model = new PropertyModel<ArrayList<ResearchObject>>(getPageReference(),
                    "page.selectedResearchObjects");
            if (!model.getObject().isEmpty()) {
                DeleteROModal deleteROModal2 = new InternalDeleteROModal("delete-ro-modal", eventBusModel, model);
                deleteROModal.replaceWith(deleteROModal2);
                deleteROModal = deleteROModal2;
                deleteROModal.show(target);
            }
        }

    }


    /**
     * A button.
     * 
     * @author piotrekhol
     * 
     */
    private class ShowCreateROModelButton extends AuthenticatedAjaxDecoratedButton {

        /** id. */
        private static final long serialVersionUID = -4510072124035188464L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket ID
         * @param form
         *            for which will be validated
         */
        public ShowCreateROModelButton(String id, Form<?> form) {
            super(id, form);
        }


        @Override
        protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onAfterSubmit(target, form);
            CreateROModal createROModal2 = new InternalCreateROModal("create-ro-modal", eventBusModel);
            createROModal.replaceWith(createROModal2);
            createROModal = createROModal2;
            createROModal.show(target);
        }

    }


    /**
     * A button.
     * 
     * @author piotrekhol
     * 
     */
    private class ShowUploadZIPModelButton extends AuthenticatedAjaxDecoratedButton {

        /** id. */
        private static final long serialVersionUID = -4510072124035188464L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket ID
         * @param form
         *            for which will be validated
         */
        public ShowUploadZIPModelButton(String id, Form<?> form) {
            super(id, form);
        }


        @Override
        protected void onAfterSubmit(AjaxRequestTarget target, Form<?> form) {
            super.onAfterSubmit(target, form);
            UploadZipModal uploadZipModal2 = new InternalUploadZipModal("zip-upload-modal", eventBusModel);
            uploadZipModal.replaceWith(uploadZipModal2);
            uploadZipModal = uploadZipModal2;
            uploadZipModal.show(target);
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
