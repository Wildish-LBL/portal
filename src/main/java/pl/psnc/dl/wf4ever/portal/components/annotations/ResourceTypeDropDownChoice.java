package pl.psnc.dl.wf4ever.portal.components.annotations;

import java.util.Collection;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.components.form.TitledDropDownMultipleChoice;
import pl.psnc.dl.wf4ever.portal.model.ResourceType;

/**
 * A convenience class for the selection of resource types.
 * 
 * @author piotrekhol
 * 
 */
public class ResourceTypeDropDownChoice extends TitledDropDownMultipleChoice<ResourceType> {

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
    public ResourceTypeDropDownChoice(String id, IModel<? extends Collection<ResourceType>> model) {
        super(id, model, ResourceType.VALUES, new ChoiceRenderer<ResourceType>("name", "uri"));
        // number of visible rows
        setMaxRows(5);
    }


    @Override
    protected Object getTitle(ResourceType choice) {
        return choice.getDefinition() != null ? choice.getDefinition() : choice.getName();
    }

}
