package pl.psnc.dl.wf4ever.portal.pages.base.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class Search extends Panel {

    private String searchKeywords;


    public Search(String id) {
        super(id);
        Form<?> searchForm = new Form<Void>("searchFormBar");
        searchForm.add(new AjaxButton("searchButtonBar", searchForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }
        });
        RequiredTextField<String> searchFieldBar = new RequiredTextField<String>("keywords", new PropertyModel<String>(
                this, "searchKeywords"));

        searchForm.add(searchFieldBar);
        add(searchForm);
    }


    public String getSearchKeywords() {
        return searchKeywords;
    }


    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

}
