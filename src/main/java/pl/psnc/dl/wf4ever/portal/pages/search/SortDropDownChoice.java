package pl.psnc.dl.wf4ever.portal.pages.search;

import java.util.List;

import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.IModel;

import pl.psnc.dl.wf4ever.portal.events.search.SortOptionChangeEvent;

/**
 * A drop down list for sort options.
 * 
 * @author piotrekhol
 * 
 */
public class SortDropDownChoice extends DropDownChoice<SortOption> {

    /** id. */
    private static final long serialVersionUID = 2380587615885283494L;


    /**
     * Constructor.
     * 
     * @param id
     *            markup id
     * @param selectedOption
     *            model getting and setting the selected sort option
     * @param choices
     *            available sort options
     */
    public SortDropDownChoice(String id, IModel<SortOption> selectedOption, List<? extends SortOption> choices) {
        super(id, selectedOption, choices, new ChoiceRenderer<SortOption>("value", "key"));
    }


    @Override
    protected boolean wantOnSelectionChangedNotifications() {
        return true;
    }


    @Override
    protected void onSelectionChanged(SortOption newSelection) {
        send(getPage(), Broadcast.BREADTH, new SortOptionChangeEvent(newSelection));
    }

}
