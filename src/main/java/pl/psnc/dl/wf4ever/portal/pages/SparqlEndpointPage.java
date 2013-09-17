package pl.psnc.dl.wf4ever.portal.pages;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.ErrorEvent;
import pl.psnc.dl.wf4ever.portal.events.sparql.ExecuteSparqlQueryEvent;
import pl.psnc.dl.wf4ever.portal.events.sparql.GenerateQueryUrlEvent;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * A RODL SPARQL web interface.
 * 
 * @author piotrekhol
 * 
 */
public class SparqlEndpointPage extends BasePage {

    /** id. */
    private static final long serialVersionUID = 1L;

    /** User query. */
    private String query;

    /** The URL to execute the query in RODL. */
    private String url;

    /** The query result. */
    private String result;

    /** text area for the query. */
    private TextArea<String> queryTA;

    /** feedback panel for the query text area. */
    private MyFeedbackPanel queryFeedback;

    /** Resulting URL. */
    private Label urlLabel;

    /** Results. */
    private Label resultLabel;


    /**
     * Constructor.
     * 
     * @param parameters
     *            page params
     */
    @SuppressWarnings("serial")
    public SparqlEndpointPage(final PageParameters parameters) {
        super(parameters);
        add(new MyFeedbackPanel("feedbackPanel"));
        Form<?> form = new Form<Void>("form");
        add(form);

        queryTA = new TextArea<String>("query", new PropertyModel<String>(this, "query")) {

            @Override
            protected void onValid() {
                add(AttributeModifier.remove("style"));
            };


            @Override
            protected void onInvalid() {
                add(AttributeModifier.replace("style", "border-color: #EE5F5B"));
            };
        };
        queryTA.setRequired(true);
        queryTA.setOutputMarkupId(true);
        form.add(queryTA);
        queryFeedback = new MyFeedbackPanel("queryFeedback");
        queryFeedback.setOutputMarkupId(true);
        form.add(queryFeedback);
        urlLabel = new Label("url", new PropertyModel<String>(this, "url"));
        urlLabel.setOutputMarkupId(true);
        form.add(urlLabel);
        resultLabel = new Label("result", new PropertyModel<String>(this, "result"));
        resultLabel.setOutputMarkupId(true);
        form.add(resultLabel);

        form.add(new AjaxEventButton("execute", form, this, ExecuteSparqlQueryEvent.class));
        form.add(new AjaxEventButton("generateURL", form, this, GenerateQueryUrlEvent.class));
    }


    @Override
    public void onEvent(IEvent<?> event) {
        if (event.getPayload() instanceof ExecuteSparqlQueryEvent) {
            onExecuteSparqlQuery((ExecuteSparqlQueryEvent) event.getPayload());
        }
        if (event.getPayload() instanceof GenerateQueryUrlEvent) {
            onGenerateQueryUrl((GenerateQueryUrlEvent) event.getPayload());
        }
        if (event.getPayload() instanceof ErrorEvent) {
            onError((ErrorEvent) event.getPayload());
        }
    }


    /**
     * Update the components after an error.
     * 
     * @param payload
     *            event
     */
    private void onError(ErrorEvent payload) {
        payload.getTarget().add(queryTA);
        payload.getTarget().add(queryFeedback);
        payload.getTarget().add(urlLabel);
    }


    /**
     * Generate the query URL and update the components.
     * 
     * @param payload
     *            event
     */
    private void onGenerateQueryUrl(GenerateQueryUrlEvent payload) {
        try {
            setUrl(getEndpointUrl().toString());
        } catch (MalformedURLException | URISyntaxException e) {
            error(e.getMessage());
        }
        payload.getTarget().add(queryTA);
        payload.getTarget().add(queryFeedback);
        payload.getTarget().add(urlLabel);
    }


    /**
     * Execute the SPARQL query.
     * 
     * @param payload
     *            event
     */
    private void onExecuteSparqlQuery(ExecuteSparqlQueryEvent payload) {
        Client client = Client.create();
        try {
            WebResource webResource = client.resource(getEndpointUrl().toString());
            String response = webResource.accept("application/x-turtle").get(String.class);
            setResult(response);
        } catch (Exception e) {
            error(e.getMessage());
        }
        payload.getTarget().add(queryTA);
        payload.getTarget().add(queryFeedback);
        payload.getTarget().add(resultLabel);
    }


    /**
     * Create a RODL SPARQL query URL.
     * 
     * @return the URL
     * @throws URISyntaxException
     *             the URI could not be created
     * @throws MalformedURLException
     *             the URI is not a valid URL
     */
    private URL getEndpointUrl()
            throws URISyntaxException, MalformedURLException {
        URI uri = ((PortalApplication) getApplication()).getSparqlEndpointURI();
        return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), "query=" + query, uri.getFragment()).toURL();
    }


    /**
     * Get an absolute URL for a Page and parameters. E.g. http://localhost/wicket/Page?param1=value
     * 
     * @param pageClass
     *            Page Class
     * @param parameters
     *            Params
     * @param <C>
     *            Page Class
     * @return Absolute Url
     */
    public static <C extends Page> String getAbsoluteUrl(final Class<C> pageClass, final PageParameters parameters) {

        CharSequence resetUrl = RequestCycle.get().urlFor(pageClass, parameters);
        String abs = RequestUtils.toAbsolutePath("/", resetUrl.toString());
        final Url url = Url.parse(abs);
        return RequestCycle.get().getUrlRenderer().renderFullUrl(url);
    }


    public String getQuery() {
        return query;
    }


    public void setQuery(String query) {
        this.query = query;
    }


    public String getResult() {
        return result;
    }


    public void setResult(String result) {
        this.result = result;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }
}
