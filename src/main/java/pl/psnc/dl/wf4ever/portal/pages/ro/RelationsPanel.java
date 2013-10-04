package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Annotation;
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.components.annotations.EditableRelationTextPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.annotations.AddAnnotationClickedEvent;
import pl.psnc.dl.wf4ever.portal.model.wicket.AnnotationTripleModel;

import com.google.common.collect.Multimap;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class RelationsPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RelationsPanel.class);

    private IModel<ResearchObject> roModel;
    private Component addAnnotationPanel;


    public RelationsPanel(String id, IModel<ResearchObject> model) {
        super(id);
        roModel = model;
        Multimap<URI, Annotation> annotationMap = model.getObject().getAllAnnotations();
        ////////////////
        ///////////
        Annotation tmpAnnotation = null;
        //////////
        ///////////////

        for (URI key : annotationMap.keys()) {
            Collection<Annotation> annotations = annotationMap.get(key);
            for (Annotation annotation : annotations) {
                //////////
                ///////////////
                tmpAnnotation = annotation;
                break;
                //////////
                ///////////////
            }
            /////////////
            //////////
            if (tmpAnnotation != null) {
                break;
            }
            /////////////
            //////////
        }

        setOutputMarkupPlaceholderTag(true);
        Form<Void> form = new Form<Void>("form");
        add(form);
        form.add(new AnnotationEditAjaxEventButton("annotate", form, model, this, AddAnnotationClickedEvent.class));

        form.add(new AnnotationTripleList("annotation-triple", new PropertyModel<List<AnnotationTriple>>(
                new AnnotationCollection(tmpAnnotation), "list")));

        addAnnotationPanel = new WebMarkupContainer("new-annotation");
        addAnnotationPanel.setOutputMarkupPlaceholderTag(true);
        addAnnotationPanel.setVisible(false);
        form.add(addAnnotationPanel);
    }


    final class AnnotationCollection {

        List<AnnotationTriple> list;


        public AnnotationCollection(Annotation annotation) {
            list = new ArrayList<AnnotationTriple>();
            list.add(new AnnotationTriple(annotation, annotation.getResearchObject(), DCTerms.accessRights, "value",
                    true));
        }


        public List<AnnotationTriple> getList() {
            return list;
        }
    }


    /**
     * A list of annotation triples.
     * 
     * @author piotrekhol
     * 
     */
    final class AnnotationTripleList extends ListView<AnnotationTriple> {

        /** id. */
        private static final long serialVersionUID = -3620346062427280309L;


        /**
         * Constructor.
         * 
         * @param id
         *            wicket id
         * @param model
         *            list of annotation triples
         */
        public AnnotationTripleList(String id, IModel<? extends List<? extends AnnotationTriple>> model) {
            super(id, model);
        }


        @Override
        protected void populateItem(ListItem<AnnotationTriple> item) {
            item.add(new EditableRelationTextPanel("editable-annotation-triple", new AnnotationTripleModel(item
                    .getModelObject()), false));
            item.setRenderBodyOnly(true);
        }

    }
}
