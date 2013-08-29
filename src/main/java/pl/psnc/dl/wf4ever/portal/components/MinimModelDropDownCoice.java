package pl.psnc.dl.wf4ever.portal.components;

import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.components.form.TitledDropDownChoice;
import pl.psnc.dl.wf4ever.portal.model.MinimModel;

/**
 * A drop down of minim models.
 * 
 * @author piotrekhol
 * 
 */
public class MinimModelDropDownCoice extends TitledDropDownChoice<MinimModel> {

    /** id. */
    private static final long serialVersionUID = -2324251722129629991L;


    /**
     * Constructor.
     * 
     * @param id
     *            See Component
     * @param model
     *            See Component
     * @param choices
     *            The drop down choices
     */
    public MinimModelDropDownCoice(String id, IModel<MinimModel> model, List<? extends MinimModel> choices) {
        super(id, model, choices, new ChoiceRenderer<MinimModel>("title", "uri"));
    }


    @Override
    protected Object getTitle(MinimModel choice) {
        return choice.getDescription();
    }

}
