package pl.psnc.dl.wf4ever.portal.pages.notifications;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.purl.wf4ever.rosrs.client.notifications.Notification;

import pl.psnc.dl.wf4ever.portal.listeners.IAjaxLinkListener;

final class NotificationsList extends PropertyListView<Notification> {

    /** id. */
    private static final long serialVersionUID = -2527527943968289889L;
    private transient DateTimeFormatter hourFormatter = new DateTimeFormatterBuilder().appendHourOfDay(2)
            .appendLiteral(":").appendMinuteOfHour(2).toFormatter();
    private transient DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder().appendDayOfMonth(1)
            .appendLiteral("/").appendMonthOfYear(1).appendLiteral("/").appendYear(2, 2).toFormatter();
    private IModel<Notification> selectedNotification;

    private List<IAjaxLinkListener> listeners = new ArrayList<>();


    NotificationsList(String id, List<? extends Notification> list, IModel<Notification> selectedNotification) {
        super(id, list);
        this.selectedNotification = selectedNotification;
    }


    @Override
    protected void populateItem(final ListItem<Notification> item) {
        item.add(new Label("title"));
        item.add(new Label("published", formatDateTime(item.getModelObject().getPublished())));
        item.add(new Label("source"));
        item.add(new AjaxEventBehavior("onclick") {

            /** id. */
            private static final long serialVersionUID = -7851716059681985652L;


            @Override
            protected void onEvent(AjaxRequestTarget target) {
                selectedNotification.setObject(item.getModelObject());
                for (IAjaxLinkListener listener : listeners) {
                    listener.onAjaxLinkClicked(item, target);
                }
            }
        });
    }


    private String formatDateTime(DateTime dateTime) {
        if ((new LocalDate(dateTime)).equals(new LocalDate())) {
            return hourFormatter.print(dateTime);
        } else {
            return dateFormatter.print(dateTime);
        }
    }


    public List<IAjaxLinkListener> getListeners() {
        return listeners;
    }

}
