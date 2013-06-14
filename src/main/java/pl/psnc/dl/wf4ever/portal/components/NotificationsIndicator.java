package pl.psnc.dl.wf4ever.portal.components;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.notifications.Notification;
import org.purl.wf4ever.rosrs.client.notifications.NotificationService;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxRedirectButton;
import pl.psnc.dl.wf4ever.portal.pages.notifications.NotificationsPage;

import com.google.common.eventbus.EventBus;

/**
 * An indicator of a number of notifications for this RO.
 * 
 * @author pejot
 * 
 */
public class NotificationsIndicator extends Panel {

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(NotificationsIndicator.class);

    /** id. */
    private static final long serialVersionUID = 1L;


    /**
     * A status to notify the users that there are recent notifications.
     * 
     * @author piotrekhol
     * 
     */
    private enum RecentStatus {
        /** The latest notification has been generated today. */
        TODAY,
        /** The latest notification has been generated in the previous 7 days. */
        THIS_WEEK,
        /** No notifications in the previous 30 days. */
        DEFAULT
    }


    /** Freshness status for the indicator. */
    private RecentStatus recentStatus = RecentStatus.DEFAULT;

    /** The main component of the indicator. */
    private AjaxRedirectButton button;

    /** The timestamp of the most recent notification. */
    private DateTime latest;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param researchObjectModel
     *            research object for this indicator
     * @param notificationsModel
     *            the model of the list of notifications of the RO
     * @param eventBusModel
     *            event bus model
     */
    public NotificationsIndicator(String id, IModel<ResearchObject> researchObjectModel,
            IModel<List<Notification>> notificationsModel, final IModel<EventBus> eventBusModel) {
        super(id, notificationsModel);
        Form<Void> form = new Form<Void>("form");
        add(form);

        PageParameters params = new PageParameters();
        params.add("ro", researchObjectModel.getObject().getUri());
        button = new AjaxRedirectButton("button", form, NotificationsPage.class, params);
        button.add(new Label("text", new PropertyModel<>(notificationsModel, "size")));
        button.add(new Behavior() {

            /** id. */
            private static final long serialVersionUID = -7063493249299862964L;


            @Override
            public void onComponentTag(Component component, ComponentTag tag) {
                super.onComponentTag(component, tag);
                switch (recentStatus) {
                    case TODAY:
                        tag.append("class", "btn-danger", " ");
                        break;
                    case THIS_WEEK:
                        tag.append("class", "btn-warning", " ");
                        break;
                    default:
                        break;
                }
                if (!component.isEnabled()) {
                    tag.append("class", "disabled", " ");
                }
                if (latest != null) {
                    tag.append("title", "Last message: " + DateTimeFormat.shortDateTime().print(latest), " ");
                } else {
                    tag.append("title", "There are no messages", " ");
                }
            }
        });
        form.add(button);
        NotificationService notificationService = new NotificationService(
                ((PortalApplication) WebApplication.get()).getRodlURI(), null);
        form.add(new ExternalLink("rss", notificationService.getNotificationsUri(
            researchObjectModel.getObject().getUri(), null, null).toString()));
    }


    @Override
    protected void onConfigure() {
        @SuppressWarnings("unchecked")
        List<Notification> notifications = (List<Notification>) getDefaultModelObject();
        if (!notifications.isEmpty()) {
            latest = notifications.get(notifications.size() - 1).getPublished();
            DateTime now = DateTime.now();
            if (now.minusHours(24).isBefore(latest)) {
                recentStatus = RecentStatus.TODAY;
            } else if (now.minusDays(7).isBefore(latest)) {
                recentStatus = RecentStatus.THIS_WEEK;
            } else {
                recentStatus = RecentStatus.DEFAULT;
            }
        }
        button.setEnabled(!notifications.isEmpty());
        super.onConfigure();
    }

}
