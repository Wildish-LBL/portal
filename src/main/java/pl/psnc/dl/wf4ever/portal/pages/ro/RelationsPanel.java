package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.event.IEvent;
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
import pl.psnc.dl.wf4ever.portal.components.annotations.NewRelationTextPanel;
import pl.psnc.dl.wf4ever.portal.components.form.AnnotationEditAjaxEventButton;
import pl.psnc.dl.wf4ever.portal.events.FolderChangeEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.FolderAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceAddReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceDeletedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMoveClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMoveEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceMovedEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.ResourceUpdateReadyEvent;
import pl.psnc.dl.wf4ever.portal.events.aggregation.UpdateClickedEvent;
import pl.psnc.dl.wf4ever.portal.events.annotations.AbstractAnnotationEditedEvent;
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
    private Component addRelationPanel;
    RelationsDict relationsDict;
    List<URI> subjectsList;
    List<URI> objectsList;
    AnnotationCollection annotationCollection;


    public RelationsPanel(String id, IModel<ResearchObject> model) {
        super(id);
        relationsDict = new RelationsDict();
        roModel = model;
        subjectsList = new ArrayList<URI>();
        subjectsList.add(model.getObject().getUri());
        annotationCollection = new AnnotationCollection();
        annotationCollection.filterTriples((model.getObject().getAnnotationTriples()));

        for (Resource r : model.getObject().getResources().values()) {
            annotationCollection.filterTriples(r.getAnnotationTriples());
            subjectsList.add(r.getUri());
        }

        for (Folder f : model.getObject().getFolders().values()) {
            annotationCollection.filterTriples(f.getAnnotationTriples());
            subjectsList.add(f.getUri());
        }

        objectsList = new ArrayList<URI>(subjectsList);
        setOutputMarkupPlaceholderTag(true);
        Form<Void> form = new Form<Void>("form");
        add(form);
        form.add(new AnnotationEditAjaxEventButton("annotate", form, model, this, AddAnnotationClickedEvent.class));
        form.add(new AnnotationTripleList("annotation-triple", new PropertyModel<List<AnnotationTriple>>(
                annotationCollection, "list")));

        addRelationPanel = new WebMarkupContainer("new-annotation");
        addRelationPanel.setOutputMarkupPlaceholderTag(true);
        addRelationPanel.setVisible(false);
        form.add(addRelationPanel);
    }


    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        if (event.getPayload() instanceof AddAnnotationClickedEvent) {
            onAnnotateClicked((AddAnnotationClickedEvent) event.getPayload());
        }
        if (event.getPayload() instanceof AbstractAnnotationEditedEvent) {
            onAnnotationEdited((AbstractAnnotationEditedEvent) event.getPayload());
        }
        //resource events

        if (event.getPayload() instanceof FolderChangeEvent) {
            onFolderChange((FolderChangeEvent) event.getPayload());
        }

        if (event.getPayload() instanceof ResourceMovedEvent) {
            onResourceMoved((ResourceMovedEvent) event.getPayload());
        }

        if (event.getPayload() instanceof ResourceAddReadyEvent) {
            onResourceAdd((ResourceAddReadyEvent) event.getPayload());
        }
        if (event.getPayload() instanceof ResourceUpdateReadyEvent) {
            onResourceUpdate((ResourceUpdateReadyEvent) event.getPayload());
        }
        if (event.getPayload() instanceof FolderAddReadyEvent) {
            onFolderAdd((FolderAddReadyEvent) event.getPayload());
        }

        if (event.getPayload() instanceof ResourceDeletedEvent) {
            onResourceDeleted((ResourceDeletedEvent) event.getPayload());
        }

    }


    private void onResourceDeleted(ResourceDeletedEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onFolderAdd(FolderAddReadyEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onResourceUpdate(ResourceUpdateReadyEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onResourceAdd(ResourceAddReadyEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onResourceMove(ResourceMoveEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onResourceMoved(ResourceMovedEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onAddFolderClicked(FolderAddClickedEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onAddResourceClicked(ResourceAddClickedEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onResourceMoveClicked(ResourceMoveClickedEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onResourceUpdateClicked(UpdateClickedEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void onFolderChange(FolderChangeEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    /**
     * Refresh the panel when an annotation changes.
     * 
     * @param event
     *            AJAX event
     */
    private void onAnnotationEdited(AbstractAnnotationEditedEvent event) {
        refresh();
        event.getTarget().add(this);
    }


    private void refresh() {
        annotationCollection.clear();
        subjectsList.clear();
        objectsList.clear();
        subjectsList.add(roModel.getObject().getUri());
        objectsList.add(roModel.getObject().getUri());

        annotationCollection.filterTriples((roModel.getObject().getAnnotationTriples()));
        for (Resource r : roModel.getObject().getResources().values()) {
            annotationCollection.filterTriples(r.getAnnotationTriples());
            subjectsList.add(r.getUri());
            objectsList.add(r.getUri());
        }

        for (Folder f : roModel.getObject().getFolders().values()) {
            annotationCollection.filterTriples(f.getAnnotationTriples());
            subjectsList.add(f.getUri());
            objectsList.add(f.getUri());
        }

    }


    /**
     * Show the new annotation panel.
     * 
     * @param event
     *            the event that triggers this action
     */
    private void onAnnotateClicked(AddAnnotationClickedEvent event) {
        NewRelationTextPanel newRelationPanel = new NewRelationTextPanel("new-annotation", roModel, true, subjectsList,
                RelationsDict.predefinedRelations, objectsList);
        addRelationPanel.replaceWith(newRelationPanel);
        addRelationPanel = newRelationPanel;
        event.getTarget().add(addRelationPanel);

    }


    final class AnnotationCollection implements Serializable {

        private static final long serialVersionUID = 1L;
        List<AnnotationTriple> list;


        public AnnotationCollection() {
            list = new ArrayList<AnnotationTriple>();
        }


        public void clear() {
            list.clear();
        }


        public void filterTriples(List<AnnotationTriple> triples) {
            for (AnnotationTriple triple : triples) {
                if (RelationsDict.predefinedRelations.contains(triple.getProperty())) {
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
            if (item.getModelObject().getValue() == null) {
                item.setVisible(false);
            }
            item.add(new EditableRelationTextPanel("editable-annotation-triple", new AnnotationTripleModel(item
                    .getModelObject()), false));
            item.setRenderBodyOnly(true);
        }
    }


    final static class RelationsDict implements Serializable {

        private static final long serialVersionUID = 1L;

        public static final List<URI> predefinedRelations = Arrays.asList(new URI[] {
                URI.create(PROV.wasDerivedFrom.getURI()), URI.create(PROV.wasRevisionOf.getURI()),
                URI.create(PROV.wasQuotedFrom.getURI()), URI.create(PROV.hadOriginalSource.getURI()),
                URI.create(WFPROV.usedInput.getURI()), URI.create(WFPROV.wasOutputFrom.getURI()),
                URI.create(WFPROV.describedByWorkflow.getURI()), URI.create(ROTERMS.inputSelected.getURI()),
                URI.create(WFDESC.hasInput.getURI()), URI.create(WFDESC.hasOutput.getURI()),
                URI.create(WFDESC.hasSubWorkflow.getURI()), URI.create(WF4EVER.rootURI.getURI()) });

    }

}
