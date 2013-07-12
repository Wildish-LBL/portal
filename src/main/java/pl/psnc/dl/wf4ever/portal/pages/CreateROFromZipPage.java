package pl.psnc.dl.wf4ever.portal.pages;

import java.io.InputStream;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.pages.ro.RoPage;
import pl.psnc.dl.wf4ever.portal.services.CreateROThread;
import pl.psnc.dl.wf4ever.portal.services.CreateROThread.ProgressModel;
import pl.psnc.dl.wf4ever.portal.services.CreateROThread.State;

/**
 * Page that displays the progress of creating a new RO from a ZIP archive of resources.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class CreateROFromZipPage extends BasePage {

    /** id. */
    private static final long serialVersionUID = -3233388849667095897L;

    /** feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** The progress of creating the RO. */
    private ProgressModel progressModel;


    /**
     * Constructor.
     * 
     * @param zip
     *            the zip file
     * @param zipName
     *            the filename of the zip file
     * 
     */
    public CreateROFromZipPage(InputStream zip, String zipName) {
        super(new PageParameters());
        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        add(feedbackPanel);
        add(new Label("name", zipName));

        CreateROThread create = new CreateROThread(zip, zipName, MySession.get().getRosrs());
        progressModel = create.getProgressModel();

        final Label completeLabel = new Label("complete", new PropertyModel<String>(this, "complete"));
        add(completeLabel.setOutputMarkupId(true));
        final Label totalLabel = new Label("total", new PropertyModel<String>(this, "total"));
        add(totalLabel.setOutputMarkupId(true));

        final TextArea<String> console = new TextArea<>("console", new PropertyModel<String>(progressModel,
                "outputString"));
        add(console);

        final Component placeholder = new WebMarkupContainer("go-to-ro").setOutputMarkupPlaceholderTag(true)
                .setVisible(false);
        add(placeholder);

        console.add(new AjaxSelfUpdatingTimerBehavior(Duration.milliseconds(500)) {

            /** id. */
            private static final long serialVersionUID = 5987637208986630961L;


            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                super.onPostProcessTarget(target);
                //                target.appendJavaScript("$(\"#progressbar\").progressbar(\"value\", " + model.getProgressInPercent()
                //                        + ");");

                target.add(completeLabel);
                target.add(totalLabel);
                target.appendJavaScript("$('#console').scrollTop($('#console')[0].scrollHeight);");
                if (progressModel.getThreadState() == State.TERMINATED) {
                    stop(target);
                    console.remove(this);
                    PageParameters params = new PageParameters();
                    params.add("ro", progressModel.getRoUri());
                    BookmarkablePageLink<String> link = new BookmarkablePageLink<>("go-to-ro", RoPage.class, params);
                    placeholder.replaceWith(link);
                    link.setOutputMarkupId(true);
                    link.setVisible(true);
                    target.add(link);
                }
            }
        });

        create.start();
    }


    public String getComplete() {
        return progressModel.getComplete() != null ? "" + progressModel.getComplete() : "?";
    }


    public String getTotal() {
        return progressModel.getTotal() != null ? "" + progressModel.getTotal() : "?";
    }
}
