package pl.psnc.dl.wf4ever.portal.pages.util;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;
import org.purl.wf4ever.rosrs.client.Creator;

/**
 * A utility panel that displays a small spinning circle while the username is being resolved.
 * 
 * @author piotrekhol
 * 
 */
public class CreatorPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -2077084041006536701L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            {@link Creator} model
     */
    @SuppressWarnings("serial")
    public CreatorPanel(String id, IModel<Creator> model) {
        super(id, model);
        final Label value = new Label("value", new PropertyModel<String>(model, "value"));
        value.setOutputMarkupId(true);
        add(value);
        final WebMarkupContainer loading = new WebMarkupContainer("loading");
        loading.setOutputMarkupId(true);
        add(loading);

        if (model.getObject() != null && model.getObject().isLoading()) {
            add(new AjaxSelfUpdatingTimerBehavior(Duration.milliseconds(1000)) {

                @Override
                protected void onPostProcessTarget(AjaxRequestTarget target) {
                    synchronized (getDefaultModelObject()) {
                        Creator creator = (Creator) getDefaultModelObject();
                        if (!creator.isLoading()) {
                            stop();
                            //							getParent().remove(this);
                            loading.setVisible(false);
                            target.add(value);
                            target.add(loading);
                        }
                    }
                }
            });
        } else {
            loading.setVisible(false);
        }
    }
}
