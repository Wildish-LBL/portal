package pl.psnc.dl.wf4ever.portal.events.annotations;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.purl.wf4ever.rosrs.client.Annotable;

/**
 * User wants to import an annotation in an RDF file.
 * 
 * @author piotrekhol
 * 
 */
public class ImportAnnotationClickedEvent extends AnnotationAddClickedEvent {

    /**
     * Constructor.
     * 
     * @param target
     *            AJAX request target
     * @param model
     *            the resource that will be annotated
     */
    public ImportAnnotationClickedEvent(AjaxRequestTarget target, IModel<? extends Annotable> model) {
        super(target, model);
    }

}
