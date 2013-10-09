package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
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
import org.purl.wf4ever.rosrs.client.AnnotationTriple;
import org.purl.wf4ever.rosrs.client.Folder;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.components.annotations.EditableRelationTextPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.annotations.AddAnnotationClickedEvent;
import pl.psnc.dl.wf4ever.portal.model.wicket.AnnotationTripleModel;
import pl.psnc.dl.wf4ever.vocabulary.PROV;
import pl.psnc.dl.wf4ever.vocabulary.ROTERMS;
import pl.psnc.dl.wf4ever.vocabulary.WF4EVER;
import pl.psnc.dl.wf4ever.vocabulary.WFDESC;
import pl.psnc.dl.wf4ever.vocabulary.WFPROV;

public class RelationsPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RelationsPanel.class);

    private IModel<ResearchObject> roModel;
    private Component addAnnotationPanel;
    RelationsDict relationsDict;


    public RelationsPanel(String id, IModel<ResearchObject> model) {
        super(id);
        relationsDict = new RelationsDict();
        roModel = model;

        AnnotationCollection annotationCollection = new AnnotationCollection();
        annotationCollection.filterTriples((model.getObject().getAnnotationTriples()));

        for (Resource r : model.getObject().getResources().values()) {
            annotationCollection.filterTriples(r.getAnnotationTriples());
        }

        for (Folder f : model.getObject().getFolders().values()) {
            annotationCollection.filterTriples(f.getAnnotationTriples());
        }
        /*
        Multimap<URI, Annotation> annotationMap = model.getObject().getAllAnnotations();
        for (URI key : annotationMap.keys()) {
            Collection<Annotation> annotations = annotationMap.get(key);
            for (Annotation annotation : annotations) {
                try {
                    annotation.load();
                } catch (ROSRSException e) {
                    LOG.warn("Tha annotation " + annotation.getUri().toString() + " can't be loaded");
                    return;
                }
                annotationCollection.filterStatements(annotation, annotation.getStatements());
            }
        }
        */

        setOutputMarkupPlaceholderTag(true);
        Form<Void> form = new Form<Void>("form");
        add(form);
        form.add(new AnnotationEditAjaxEventButton("annotate", form, model, this, AddAnnotationClickedEvent.class));
        form.add(new AnnotationTripleList("annotation-triple", new PropertyModel<List<AnnotationTriple>>(
                annotationCollection, "list")));

        addAnnotationPanel = new WebMarkupContainer("new-annotation");
        addAnnotationPanel.setOutputMarkupPlaceholderTag(true);
        addAnnotationPanel.setVisible(false);
        form.add(addAnnotationPanel);
    }


    final class AnnotationCollection implements Serializable {

        private static final long serialVersionUID = 1L;
        List<AnnotationTriple> list;


        public AnnotationCollection() {
            list = new ArrayList<AnnotationTriple>();
        }


        public void filterTriples(List<AnnotationTriple> triples) {
            for (AnnotationTriple triple : triples) {
                if (relationsDict.predefinedRelations.contains(triple.getProperty())) {
                    list.add(triple);
                }
            }
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


    final class RelationsDict implements Serializable {

        private static final long serialVersionUID = 1L;

        List<URI> predefinedRelations = new ArrayList<URI>();


        public RelationsDict() {
            //PROV
            predefinedRelations.add(URI.create(PROV.wasDerivedFrom.getURI()));
            predefinedRelations.add(URI.create(PROV.wasRevisionOf.getURI()));
            predefinedRelations.add(URI.create(PROV.wasQuotedFrom.getURI()));
            predefinedRelations.add(URI.create(PROV.hadOriginalSource.getURI()));
            predefinedRelations.add(URI.create(WFPROV.usedInput.getURI()));
            predefinedRelations.add(URI.create(WFPROV.wasOutputFrom.getURI()));
            predefinedRelations.add(URI.create(WFPROV.describedByWorkflow.getURI()));
            predefinedRelations.add(URI.create(ROTERMS.inputSelected.getURI()));
            predefinedRelations.add(URI.create(WFDESC.hasInput.getURI()));
            predefinedRelations.add(URI.create(WFDESC.hasOutput.getURI()));
            predefinedRelations.add(URI.create(WFDESC.hasSubWorkflow.getURI()));
            predefinedRelations.add(URI.create(WF4EVER.rootURI.getURI()));
        }


        public List<URI> getPredefinedRelations() {
            return predefinedRelations;
        }
    }

}
