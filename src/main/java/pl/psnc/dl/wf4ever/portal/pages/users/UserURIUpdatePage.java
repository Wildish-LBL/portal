package pl.psnc.dl.wf4ever.portal.pages.users;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.purl.wf4ever.rosrs.client.common.ROSRSException;
import org.purl.wf4ever.rosrs.client.common.users.MigrateService;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.TemplatePage;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.services.RODLUtilities;

/**
 * Decide whether to migrate the OpenID.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class UserURIUpdatePage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = -8975579933617712699L;

    /** Old user URI. */
    private URI oldURI;

    /** New user URI. */
    private URI newURI;

    /** The current status. */
    private String status;

    /** Status label. */
    private Label statusLabel;

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters
     */
    @SuppressWarnings("serial")
    public UserURIUpdatePage(PageParameters pageParameters) {
        super(pageParameters);
        setDefaultModel(new CompoundPropertyModel<UserURIUpdatePage>(this));

        String token = pageParameters.get("token").toString();
        try {
            oldURI = RODLUtilities.getUser(token,
                ((PortalApplication) PortalApplication.get()).getRodlURI().resolve("../rosrs5/")).getURI();
        } catch (URISyntaxException | ROSRSException e) {
            error(e);
        }
        newURI = MySession.get().getUser().getURI();

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        add(new Label("oldURI"));
        add(new Label("newURI"));
        status = "Updating the RODL...";
        statusLabel = new Label("status");
        statusLabel.setOutputMarkupId(true);
        add(statusLabel);

        add(new AbstractDefaultAjaxBehavior() {

            @Override
            protected void respond(AjaxRequestTarget target) {
                try {
                    status = MigrateService.updateUserURI(getRodlURI(), oldURI, newURI);
                } catch (Exception e) {
                    status = "Error";
                    error(e);
                    target.add(feedbackPanel);
                }
                target.add(statusLabel);
            }


            @Override
            public void renderHead(final Component component, final IHeaderResponse response) {
                super.renderHead(component, response);
                response.renderOnDomReadyJavaScript(getCallbackScript().toString());
            }
        });
    }


    public URI getOldURI() {
        return oldURI;
    }


    public URI getNewURI() {
        return newURI;
    }


    public String getStatus() {
        return status;
    }
}
