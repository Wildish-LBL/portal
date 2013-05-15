package pl.psnc.dl.wf4ever.portal.pages.notifications;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.purl.wf4ever.rosrs.client.exception.NotificationsException;
import org.purl.wf4ever.rosrs.client.notifications.Notification;
import org.purl.wf4ever.rosrs.client.notifications.NotificationService;

import pl.psnc.dl.wf4ever.portal.listeners.IAjaxLinkListener;
import pl.psnc.dl.wf4ever.portal.pages.base.Base;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

/**
 * The home page.
 * 
 * @author piotrekhol
 * 
 */
public class NotificationsPage extends Base {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(NotificationsPage.class);


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     * @throws IOException
     *             can't connect to RODL
     */
    public NotificationsPage(final PageParameters parameters)
            throws IOException {
        super(parameters);
        setDefaultModel(new CompoundPropertyModel<NotificationsPage>(this));

        final MyFeedbackPanel feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        CompoundPropertyModel<Notification> selectedNotification = new CompoundPropertyModel<Notification>(
                (Notification) null);

        URI researchObjectUri = parameters.get("ro").isNull() ? null : URI.create(parameters.get("ro").toString());

        List<Notification> notifications = null;
        NotificationService notificationService = new NotificationService(getRodlURI(), null);
        try {
            notifications = notificationService.getNotifications(researchObjectUri, null, null);
        } catch (NotificationsException e) {
            error(e.getMessage());
            LOGGER.error("Can't load notifications", e);
        }

        NotificationsList notificationsList = new NotificationsList("notificationsList", notifications,
                selectedNotification);
        add(notificationsList);
        final NotificationPanel notificationPanel = new NotificationPanel("notificationPanel", selectedNotification);
        notificationPanel.setOutputMarkupId(true);
        add(notificationPanel);

        notificationsList.getListeners().add(new IAjaxLinkListener() {

            /** id. */
            private static final long serialVersionUID = 4881609835726044109L;


            @Override
            public void onAjaxLinkClicked(Object source, AjaxRequestTarget target) {
                target.add(notificationPanel);
            }
        });
    }


    protected List<Notification> getMockNotifications() {
        List<Notification> notifications = new ArrayList<>();
        URI source = URI.create("http://sandbox.wf4ever-project.org/roevaluate/");
        URI ro = URI.create("http://example.org/rodl/ROs/myGenes/");
        Notification n1 = new Notification("urn:X-rodl:1", "Title 1",
                "<h1>It works!</h1><p>And now for something completely different</p>");
        n1.setPublished(DateTime.now());
        n1.setResearchObjectUri(ro);
        n1.setSource(source);
        notifications.add(n1);

        Notification n2 = new Notification("urn:X-rodl:2",
                "This is a very long title: Diet variability and reproductive "
                        + "performance of macaroni penguins Eudyptes chrysolophus at Bird Island," + " South Georgia",
                "<h1>Claire M. Waluda*, Simeon L. Hill, Helen J. Peat, Philip N. Trathan</h1>"
                        + "<h2>British Antarctic Survey, Natural Environment Research Council, High Cross, "
                        + "Madingley Road, Cambridge, CB3 0ET, UK</h2>"
                        + "<p><small>*Email: clwa@bas.ac.uk</small></p>"
                        + "<p>ABSTRACT: We analysed summer diet and fledging mass of macaroni penguins "
                        + "Eudyptes chrysolophus breeding at Bird Island, South Georgia, during the crèche "
                        + "period (January and February) between 1989 and 2010. Crustaceans were the main "
                        + "prey accounting, for over 90% of the diet by mass. Antarctic krill Euphausia "
                        + "superba was the main prey, in 17 out of 22 years. Amphipods Themisto gaudichaudii "
                        + "were the main prey in 1994 and 2009, fish in 2004, and the euphausiids Thysanoessa "
                        + "spp. and Euphausia frigida in 2000. There was no clearly dominant prey group in "
                        + "1999. Prey diversity and the frequency occurrence of T. gaudichaudii both increased "
                        + "with a decreasing proportion of E. superba in the diet. There was strong evidence "
                        + "that macaroni penguins have a sigmoidal functional response, indicating that this "
                        + "kind of response should be accounted for when devising ecosystem-based management "
                        + "reference points for seabirds. The energy and mass of all euphausiids combined (rather "
                        + "than E. superba in particular) in the diet were the most reliable predictors of "
                        + "chick fledging mass; the correlation between model-predicted and observed values "
                        + "was 0.84. The gross energy content of individual meals was often above average in "
                        + "years when the diets contained fewer euphausiids, but fledging mass was always "
                        + "below average in these years. Although macaroni penguins are able to feed on a "
                        + "variety of prey types, chick growth was always severely impacted by a shortage of "
                        + "euphausiids due to higher energy or time costs associated with feeding on alternative "
                        + "prey types. Given their reliance on euphausiids, macaroni penguins would be particularly "
                        + "vulnerable to potential climate-driven declines in krill stocks.</p>");
        n2.setPublished(DateTime.now());
        n2.setResearchObjectUri(ro);
        n2.setSource(source);
        notifications.add(n2);

        Notification n3 = new Notification("urn:X-rodl:3", "Title 3", "This is a text with no HTML tags.");
        n3.setPublished(ISODateTimeFormat.dateTimeParser().parseDateTime("2005-07-21T11:45:07Z"));
        n3.setResearchObjectUri(ro);
        n3.setSource(source);
        notifications.add(n3);

        return notifications;
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(new PackageResourceReference(NotificationsPage.class, "notifications.css"));
    }
}
