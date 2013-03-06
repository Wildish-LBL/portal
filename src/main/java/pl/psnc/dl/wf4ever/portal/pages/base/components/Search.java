package pl.psnc.dl.wf4ever.portal.pages.base.components;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.SearchResult;
import org.purl.wf4ever.rosrs.client.search.SearchServer;
import org.purl.wf4ever.rosrs.client.search.SparqlSearchServer;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.pages.search.SearchResultsPage;

public class Search extends Panel {

    /** id. */
    private static final long serialVersionUID = 6045458615921445179L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(Search.class);

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
                URI sparqlEndpointUri = ((PortalApplication) getApplication()).getSparqlEndpointURI();
                SearchServer searchServer = new SparqlSearchServer(sparqlEndpointUri);
                try {
                    List<SearchResult> searchResults = searchServer.search(searchKeywords);
                    setResponsePage(new SearchResultsPage(searchResults));
                } catch (SearchException e) {
                    error(e.getMessage());
                    LOGGER.error("Can't do the search for " + searchKeywords, e);
                }
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
