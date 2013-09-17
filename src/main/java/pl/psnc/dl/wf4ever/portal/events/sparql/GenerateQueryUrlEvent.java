package pl.psnc.dl.wf4ever.portal.events.sparql;

import org.apache.wicket.ajax.AjaxRequestTarget;

import pl.psnc.dl.wf4ever.portal.events.AbstractAjaxEvent;
import pl.psnc.dl.wf4ever.portal.events.AbstractClickAjaxEvent;

/**
 * User wants to generate a SPARQL query URL.
 * 
 * @author piotrekhol
 * 
 */
public class GenerateQueryUrlEvent extends AbstractAjaxEvent implements AbstractClickAjaxEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     */
    public GenerateQueryUrlEvent(AjaxRequestTarget target) {
        super(target);
    }

}
