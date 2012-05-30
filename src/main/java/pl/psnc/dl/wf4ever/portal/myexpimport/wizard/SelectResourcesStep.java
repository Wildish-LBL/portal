package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.PatternValidator;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.myexpimport.model.FileHeader;
import pl.psnc.dl.wf4ever.portal.pages.ErrorPage;
import pl.psnc.dl.wf4ever.portal.pages.ImmediateRedirectPage;
import pl.psnc.dl.wf4ever.portal.services.MyExpApi;
import pl.psnc.dl.wf4ever.portal.services.MyExpImportService;

/**
 * Step of selecting resources to import.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class SelectResourcesStep extends WizardStep {

    /** id. */
    private static final long serialVersionUID = -7984392838783804920L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(SelectResourcesStep.class);

    /** Public pack form. */
    private Form<?> publicPackForm;

    /** Public workflow form. */
    private Form<Void> publicWorkflowForm;

    /** A div with lists of personal items. */
    private final WebMarkupContainer personalItems;

    /** List of personal files. */
    private ResourceListPanel filesDiv;

    /** List of personal workflows. */
    private ResourceListPanel workflowsDiv;

    /** List of personal packs. */
    private ResourceListPanel packsDiv;


    /**
     * Constructor.
     * 
     * @param model
     *            model
     */
    @SuppressWarnings("serial")
    public SelectResourcesStep(IModel<?> model) {
        super("Select resources", null, model);

        personalItems = new WebMarkupContainer("personalItems");
        personalItems.setOutputMarkupId(true);
        personalItems.setOutputMarkupPlaceholderTag(true);
        add(personalItems);

        personalItems.add(new Label("myExpUser.name"));
        personalItems.add(new Label("myExpUser.packs.size"));
        personalItems.add(new Label("myExpUser.workflows.size"));
        personalItems.add(new Label("myExpUser.files.size"));

        filesDiv = new ResourceListPanel("filesDiv", "Files", new PropertyModel<List<FileHeader>>(getDefaultModel(),
                "myExpUser.files"), new PropertyModel<List<FileHeader>>(getDefaultModel(), "selectedFiles"));
        filesDiv.setOutputMarkupId(true);
        filesDiv.setOutputMarkupPlaceholderTag(true);
        personalItems.add(filesDiv);
        workflowsDiv = new ResourceListPanel("workflowsDiv", "Workflows", new PropertyModel<List<FileHeader>>(
                getDefaultModel(), "myExpUser.workflows"), new PropertyModel<List<FileHeader>>(getDefaultModel(),
                "selectedWorkflows"));
        workflowsDiv.setOutputMarkupId(true);
        workflowsDiv.setOutputMarkupPlaceholderTag(true);
        personalItems.add(workflowsDiv);
        packsDiv = new ResourceListPanel("packsDiv", "Packs", new PropertyModel<List<FileHeader>>(getDefaultModel(),
                "myExpUser.packs"), new PropertyModel<List<FileHeader>>(getDefaultModel(), "selectedPacks"));
        packsDiv.setOutputMarkupId(true);
        packsDiv.setOutputMarkupPlaceholderTag(true);
        personalItems.add(packsDiv);

        publicPackForm = new Form<Void>("publicPackForm");
        TextField<String> customPackId = new TextField<String>("publicPackId");
        customPackId.add(new PatternValidator("[0-9]+"));
        publicPackForm.add(customPackId);
        add(publicPackForm);

        publicWorkflowForm = new Form<Void>("publicWorkflowForm");
        TextField<String> customWorkflowId = new TextField<String>("publicWorkflowId");
        customWorkflowId.add(new PatternValidator("[0-9]+"));
        publicWorkflowForm.add(customWorkflowId);
        add(publicWorkflowForm);

        add(new IFormValidator() {

            @Override
            public void validate(Form<?> form) {
                if (filesDiv != null) {
                    filesDiv.commit();
                }
                if (workflowsDiv != null) {
                    workflowsDiv.commit();
                }
                if (packsDiv != null) {
                    packsDiv.commit();
                }
                publicPackForm.process(null);
                publicWorkflowForm.process(null);
                if (!((ImportModel) getDefaultModelObject()).isValid()) {
                    error("You must select at least one resource.");
                }
            }


            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return null;
            }
        });
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        ImportModel model = (ImportModel) getDefaultModelObject();
        personalItems.setVisible(false);
        publicPackForm.setVisible(false);
        publicWorkflowForm.setVisible(false);
        switch (model.getImportedData()) {
            case PUBLIC_PACK:
                publicPackForm.setVisible(true);
                break;
            case PUBLIC_WORKFLOW:
                publicWorkflowForm.setVisible(true);
                break;
            case PERSONAL_ITEMS:
            default:
                if (MySession.get().getMyExpAccessToken() != null) {
                    PortalApplication app = (PortalApplication) getApplication();
                    OAuthService service = MyExpApi.getOAuthService(app.getMyExpConsumerKey(),
                        app.getMyExpConsumerSecret(), app.getCallbackURL());
                    try {
                        model.setMyExpUser(MyExpImportService.retrieveMyExpUser(MySession.get().getMyExpAccessToken(),
                            service));
                    } catch (Exception e) {
                        LOG.error(e);
                        throw new RestartResponseException(ErrorPage.class, new PageParameters().add("message",
                            e.getMessage() != null ? e.getMessage() : "Unknown error"));
                    }
                    personalItems.setVisible(true);
                    filesDiv.setVisible(!model.getMyExpUser().getFiles().isEmpty());
                    workflowsDiv.setVisible(!model.getMyExpUser().getWorkflows().isEmpty());
                    packsDiv.setVisible(!model.getMyExpUser().getPacks().isEmpty());
                } else {
                    startMyExpAuthorization();
                }
                break;
        }
    }


    /**
     * Redirect to myExperiment OAuth authorization endpoint.
     */
    private void startMyExpAuthorization() {
        PortalApplication app = (PortalApplication) getApplication();
        OAuthService service = MyExpApi.getOAuthService(app.getMyExpConsumerKey(), app.getMyExpConsumerSecret(),
            app.getCallbackURL());
        Token requestToken = service.getRequestToken();
        MySession.get().setRequestToken(requestToken);
        final String authorizationUrl = service.getAuthorizationUrl(requestToken);
        throw new RestartResponseAtInterceptPageException(ImmediateRedirectPage.class, new PageParameters().add(
            "redirectTo", authorizationUrl));
    }

}
