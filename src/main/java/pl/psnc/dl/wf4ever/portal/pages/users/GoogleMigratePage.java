package pl.psnc.dl.wf4ever.portal.pages.users;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.pages.TemplatePage;

/**
 * Decide whether to migrate the OpenID.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class GoogleMigratePage extends TemplatePage {

    /** id. */
    private static final long serialVersionUID = -8975579933617712699L;


    /**
     * Constructor.
     * 
     * @param pageParameters
     *            page parameters
     */
    @SuppressWarnings("serial")
    public GoogleMigratePage(PageParameters pageParameters) {
        super(pageParameters);

        Form<?> form = new Form<Void>("migrateForm");
        add(form);
        form.add(new Button("cancel") {

            @Override
            public void onSubmit() {
                super.onSubmit();
                throw new RestartResponseException(getApplication().getHomePage());
            }
        });
        form.add(new Button("migrate") {

            @Override
            public void onSubmit() {
                // TODO Auto-generated method stub
                super.onSubmit();
            }
        });
    }

}
