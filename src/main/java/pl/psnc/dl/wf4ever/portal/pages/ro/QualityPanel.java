package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.checklist.client.ChecklistEvaluationService;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.LoadingCircle;
import pl.psnc.dl.wf4ever.portal.components.MinimModelDropDownCoice;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.components.quality.EvaluationPanel;
import pl.psnc.dl.wf4ever.portal.events.MinimModelChangedEvent;
import pl.psnc.dl.wf4ever.portal.model.MinimModel;

/**
 * A panel for selecting a minim model and seeing the results of the RO evaluation.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class QualityPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(QualityPanel.class);

    /** The panel with results. */
    private WebMarkupContainer evaluationPanel;

    /** Currently selected minim model. */
    private IModel<MinimModel> minimModel;

    /** Checklist service client. */
    private ChecklistEvaluationService service;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            RO to evaluate
     * @param service
     *            checklist service client
     * @param minimModels
     *            list of available minim models
     */
    public QualityPanel(String id, final IModel<ResearchObject> model, final ChecklistEvaluationService service,
            final List<MinimModel> minimModels) {
        super(id, model);
        this.service = service;
        Form<Void> form = new Form<>("form");
        add(form);
        minimModel = new Model<>(null);
        final MinimModelDropDownCoice dropdown = new MinimModelDropDownCoice("minim-models", minimModel, minimModels);
        dropdown.setOutputMarkupId(true);
        form.add(dropdown);
        dropdown.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            /** id. */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                reevaluate(target);
                target.add(dropdown);
            }
        });
        form.add(new AjaxEventButton("reload", form, this, MinimModelChangedEvent.class));
        add(new Label("minim-model-description", new PropertyModel<String>(minimModel, "description")));
        evaluationPanel = new WebMarkupContainer("evaluation-panel");
        evaluationPanel.setOutputMarkupId(true);
        add(evaluationPanel);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof MinimModelChangedEvent) {
            reevaluate(((MinimModelChangedEvent) event.getPayload()).getTarget());
        }
    }


    /**
     * Reevaluate the research object and refresh the outcome.
     * 
     * @param target
     *            AJAX target
     */
    private void reevaluate(AjaxRequestTarget target) {
        WebMarkupContainer evaluationPanel2 = new AjaxLazyLoadPanel("evaluation-panel", getDefaultModel()) {

            /** id. */
            private static final long serialVersionUID = 8101533630015610845L;


            @SuppressWarnings("unchecked")
            @Override
            public Component getLazyLoadComponent(String id) {
                return new EvaluationPanel(id, (IModel<ResearchObject>) getDefaultModel(), service, minimModel);
            }


            @Override
            public Component getLoadingComponent(String markupId) {
                return new LoadingCircle(markupId, "Evaluating the research object...");
            }
        };
        evaluationPanel2.setOutputMarkupId(true);
        evaluationPanel.replaceWith(evaluationPanel2);
        evaluationPanel = evaluationPanel2;
        target.add(evaluationPanel);
    }
}
