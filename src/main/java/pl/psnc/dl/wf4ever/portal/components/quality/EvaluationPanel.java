package pl.psnc.dl.wf4ever.portal.components.quality;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.purl.wf4ever.checklist.client.ChecklistEvaluationService;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.model.MinimModel;

public class EvaluationPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3738293080340212130L;


    public EvaluationPanel(String id, IModel<ResearchObject> researchObjectModel, ChecklistEvaluationService service,
            IModel<MinimModel> minimModel) {
        super(id, researchObjectModel);
        MinimModel minim = minimModel.getObject();
        String html = service
                .evaluateHtml(researchObjectModel.getObject().getUri(), minim.getUri(), minim.getPurpose());
        // skip the headers, titles, etc
        Pattern bodyPattern = Pattern.compile(".*<div class=\"body\">(.*?)</div>.*", Pattern.DOTALL);
        Matcher m = bodyPattern.matcher(html);
        m.find();
        html = m.group(1);
        add(new Label("result", html).setEscapeModelStrings(false));
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(EvaluationPanel.class,
                "EvaluationPanel.css")));
    }
}
