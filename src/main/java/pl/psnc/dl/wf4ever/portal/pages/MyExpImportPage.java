/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.myexpimport.wizard.ImportWizard;

/**
 * This page contains the myExperiment import wizard.
 * 
 * @author Piotr Hołubowicz
 * 
 */
@AuthorizeInstantiation("USER")
public class MyExpImportPage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = 4637256013660809942L;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page params
     */
    public MyExpImportPage(PageParameters pageParameters) {
        super(pageParameters);

        add(new ImportWizard("wizard"));
    }

}
