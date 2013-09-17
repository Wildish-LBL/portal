package pl.psnc.dl.wf4ever.portal.events.sparql;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to execute a SPARQL query.
 * 
 * @author piotrekhol
 * 
 */
public class ExecuteSparqlQueryEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public ExecuteSparqlQueryEvent(AjaxRequestTarget target) {
        super(target);
    }

}
