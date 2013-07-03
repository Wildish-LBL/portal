package pl.psnc.dl.wf4ever.portal.components.annotations;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.components.form.TitledDropDownChoice;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;

/**
 * A convenience class for the selection of resource types.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTypeDropDownChoice extends TitledDropDownChoice<ResourceType> {

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
    public ResourceTypeDropDownChoice(String id, IModel<ResourceType> model) {
        super(id, model, ResourceType.VALUES, new ChoiceRenderer<ResourceType>("name", "uri"));
        setNullValid(true);
    }


    @Override
    protected Object getTitle(ResourceType choice) {
        return choice.getDefinition() != null ? choice.getDefinition() : choice.getName();
    }

}
