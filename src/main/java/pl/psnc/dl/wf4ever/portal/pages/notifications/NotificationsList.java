package pl.psnc.dl.wf4ever.portal.pages.notifications;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.purl.wf4ever.rosrs.client.notifications.Notification;

final class NotificationsList extends PropertyListView<Notification> {

    /** id. */
    private static final long serialVersionUID = -2527527943968289889L;
    private DateTimeFormatter hourFormatter = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(":")
            .appendMinuteOfHour(2).toFormatter();
    private DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder().appendDayOfMonth(1).appendLiteral("/")
            .appendMonthOfYear(1).appendLiteral("/").appendYear(2, 2).toFormatter();


    NotificationsList(String id, List<? extends Notification> list) {
        super(id, list);
    }


    @Override
    protected void populateItem(ListItem<Notification> item) {
        item.add(new Label("title"));
        item.add(new Label("published", formatDateTime(item.getModelObject().getPublished())));
        item.add(new Label("source"));
    }


    private String formatDateTime(DateTime dateTime) {
        if ((new LocalDate(dateTime)).equals(new LocalDate())) {
            return hourFormatter.print(dateTime);
        } else {
            return dateFormatter.print(dateTime);
        }
    }
}
