package pl.psnc.dl.wf4ever.portal.myexpimport.wizard;

import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.Model;

import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportModel.ImportedData;

/**
 * The introduction step.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class StartImportStep extends WizardStep {

    /** id. */
    private static final long serialVersionUID = 4637256013660809942L;


    /**
     * Constructor.
     */
    public StartImportStep() {
        super("Start", null);

        Form<?> form = new Form<Void>("importedDataForm");
        add(form);

        RadioGroup<ImportedData> radioGroup = new RadioGroup<ImportedData>("importedData");
        form.add(radioGroup);
        radioGroup.add(new Radio<ImportedData>("publicPack", new Model<ImportedData>(ImportedData.PUBLIC_PACK)));
        radioGroup
                .add(new Radio<ImportedData>("publicWorkflow", new Model<ImportedData>(ImportedData.PUBLIC_WORKFLOW)));
        radioGroup.add(new Radio<ImportedData>("personalItems", new Model<ImportedData>(ImportedData.PERSONAL_ITEMS)));
    }

}
