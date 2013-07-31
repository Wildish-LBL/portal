package pl.psnc.dl.wf4ever.portal.components.annotations;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.components.form.TitledDropDownChoice;
import pl.psnc.dl.wf4ever.portal.model.template.ResearchObjectTemplate;

/**
 * A convenience class for the selection of research object templates.
 * 
 * @author piotrekhol
 * 
 */
public class TemplateDropDownChoice extends TitledDropDownChoice<ResearchObjectTemplate> {

    /** id. */
    private static final long serialVersionUID = 5494577273033932953L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param model
     *            resource type model for getting and setting the selected value
     */
    public TemplateDropDownChoice(String id, IModel<ResearchObjectTemplate> model) {
        super(id, model, ResearchObjectTemplate.VALUES, new ChoiceRenderer<ResearchObjectTemplate>("title", "title"));
        setNullValid(true);
    }


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public TemplateDropDownChoice(String id) {
        super(id, null, ResearchObjectTemplate.VALUES, new ChoiceRenderer<ResearchObjectTemplate>("title", "title"));
        setNullValid(true);
    }


    @Override
    protected Object getTitle(ResearchObjectTemplate choice) {
        return choice.getDescription() != null ? choice.getDescription() : choice.getTitle();
    }

}
