package pl.psnc.dl.wf4ever.portal.pages.ro.notifications;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.purl.wf4ever.rosrs.client.notifications.Notification;

import pl.psnc.dl.wf4ever.portal.components.EventPanel;
import pl.psnc.dl.wf4ever.portal.events.ResourceSelectedEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * A preview of a notification.
 * 
 * @author piotrekhol
 * 
 */
public class NotificationPreviewPanel extends EventPanel {

    /** id. */
    private static final long serialVersionUID = 2297121898862626801L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            notification model
     * @param eventBusModel
     *            event bus for knowing when to refresh
     */
    public NotificationPreviewPanel(String id, CompoundPropertyModel<Notification> model, IModel<EventBus> eventBusModel) {
        super(id, model, eventBusModel);
        setOutputMarkupId(true);
        add(new Label("title"));
        add(new Label("published", new PublishedDateModel()));
        add(new Label("sourceName"));
        add(new Label("content").setEscapeModelStrings(false));
    }


    /**
     * Update the notification panel when a notification is selected.
     * 
     * @param event
     *            AJAX event
     */
    @Subscribe
    public void onNotificationSelected(ResourceSelectedEvent event) {
        event.getTarget().add(this);
    }


    /**
     * Return a nicely formatted date.
     * 
     * @author piotrekhol
     * 
     */
    class PublishedDateModel extends Model<String> {

        /** id. */
        private static final long serialVersionUID = -8334879852621791873L;

        /** formatter for a full date. */
        private transient DateTimeFormatter fullDateFormatter = buildDateTimeFormatter();


        /**
         * Build the date formatter.
         * 
         * @return the full date time formatter
         */
        protected DateTimeFormatter buildDateTimeFormatter() {
            return new DateTimeFormatterBuilder().appendMonthOfYearText().appendLiteral(' ').appendDayOfMonth(1)
                    .appendLiteral(", ").appendYear(4, 4).appendLiteral(" ").appendHourOfDay(2).appendLiteral(":")
                    .appendMinuteOfHour(2).toFormatter();
        }


        /**
         * Create a date formatter if necessary and return it.
         * 
         * @return a date formatter
         */
        protected DateTimeFormatter getDateTimeFormatter() {
            if (fullDateFormatter == null) {
                fullDateFormatter = buildDateTimeFormatter();
            }
            return fullDateFormatter;
        }


        @Override
        public String getObject() {
            Notification notification = (Notification) getDefaultModelObject();
            return notification != null ? getDateTimeFormatter().print(notification.getPublished()) : null;
        }

    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(NotificationPreviewPanel.class,
                "notifications.css")));
    }
}
