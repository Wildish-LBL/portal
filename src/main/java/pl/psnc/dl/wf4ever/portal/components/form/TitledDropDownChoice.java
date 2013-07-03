package pl.psnc.dl.wf4ever.portal.components.form;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;

/**
 * A dropdown choice that displays titles in the options.
 * 
 * Taken from http://stackoverflow.com/questions/12234738/wicket-dropdownchoice-titles-tooltips-for-options.
 * 
 * @author piotrekhol
 * 
 * @param <T>
 */
public class TitledDropDownChoice<T> extends DropDownChoice<T> {

    /** id. */
    private static final long serialVersionUID = 7810861658724643360L;


    /**
     * Constructor.
     * 
     * @param id
     *            See Component
     * @param model
     *            See Component
     * @param choices
     *            The drop down choices
     * @param renderer
     *            The rendering engine
     */
    public TitledDropDownChoice(String id, IModel<T> model, List<? extends T> choices,
            IChoiceRenderer<? super T> renderer) {
        super(id, model, choices, renderer);
    }


    @Override
    protected void appendOptionHtml(AppendingStringBuffer buffer, T choice, int index, String selected) {

        super.appendOptionHtml(buffer, choice, index, selected);

        // converts <option value="foo">bar</option> to
        // <option value="foo" title="bar">bar</option>
        String replString = "value=\"" + getChoiceRenderer().getIdValue(choice, index) + "\"";
        int pos = buffer.indexOf(replString);
        buffer.insert(pos + replString.length(), " title=\"" + getTitle(choice) + "\"");

    }


    /**
     * Return the title for that option.
     * 
     * @param choice
     *            Choice object
     * @return the title to show as a hint in the drop down
     */
    protected Object getTitle(T choice) {
        return getChoiceRenderer().getDisplayValue(choice);
    }
}
