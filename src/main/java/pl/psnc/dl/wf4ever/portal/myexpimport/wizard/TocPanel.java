/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import java.util.Iterator;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.IWizardModelListener;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

/**
 * List of steps on the left side of the wizard.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class TocPanel extends Panel implements IWizardModelListener {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket:id
     * @param model
     *            wizard model
     */
    public TocPanel(String id, IWizardModel model) {
        super(id, new Model<IWizardModel>(model));

        add(getStepList(model));

        model.addListener(this);
    }


    /**
     * Return a component with a list of steps.
     * 
     * @param model
     *            wizard model
     * @return a RepeatingView with step list
     */
    private RepeatingView getStepList(IWizardModel model) {
        Iterator<IWizardStep> it = model.stepIterator();
        RepeatingView view = new RepeatingView("repeater");
        while (it.hasNext()) {
            IWizardStep step = it.next();
            Label label = new Label(view.newChildId(), new PropertyModel<WizardStep>(step, "title"));
            if (step.equals(model.getActiveStep())) {
                label.add(new AttributeModifier("class", new Model<String>("selectedStep")));
            }
            view.add(label);
        }
        return view;
    }


    @Override
    public void onActiveStepChanged(IWizardStep newStep) {
        addOrReplace(getStepList((IWizardModel) getDefaultModelObject()));
    }


    @Override
    public void onCancel() {
        // do nothing
    }


    @Override
    public void onFinish() {
        // do nothing
    }

}
