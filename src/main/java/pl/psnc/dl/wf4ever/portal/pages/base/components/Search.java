package pl.psnc.dl.wf4ever.portal.pages.base.components;

import java.net.URI;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.SearchResult;
import pl.psnc.dl.wf4ever.portal.pages.search.SearchResultsPage;
import pl.psnc.dl.wf4ever.portal.services.SearchService;

public class Search extends Panel {

    /** id. */
    private static final long serialVersionUID = 6045458615921445179L;

    /** the string that user inputs. */
    private String searchKeywords;


    public Search(String id) {
        super(id);
        Form<?> searchForm = new Form<Void>("searchFormBar") {

            /** id. */
            private static final long serialVersionUID = -9140218085617734421L;


            @Override
            protected void onSubmit() {
                super.onSubmit();
                String[] keywords = searchKeywords.split(" ");
                URI sparqlEndpointUri = ((PortalApplication) getApplication()).getSparqlEndpointURI();
                List<SearchResult> searchResults = SearchService.findUsingSparql(sparqlEndpointUri, keywords);
                setResponsePage(new SearchResultsPage(searchResults));
            }
        };
        searchForm.add(new Button("searchButtonBar"));
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
