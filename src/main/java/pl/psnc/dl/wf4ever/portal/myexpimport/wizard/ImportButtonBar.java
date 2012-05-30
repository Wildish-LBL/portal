/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.WizardButtonBar;

/**
 * Buttons bar at the bottom of the wizard.
 * 
 * @author piotrhol
 * 
 */
public class ImportButtonBar extends WizardButtonBar {

    /** id. */
    private static final long serialVersionUID = -8670230514698828604L;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param wizard
     *            wizard it belongs to
     */
    public ImportButtonBar(String id, IWizard wizard) {
        super(id, wizard);
    }

}
