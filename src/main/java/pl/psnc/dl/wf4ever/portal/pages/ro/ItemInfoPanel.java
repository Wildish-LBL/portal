package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.services.StabilityService;

/**
 * Aggregated resource panel.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ItemInfoPanel extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ItemInfoPanel.class);

    /** Div about the resource creator. */
    private final WebMarkupContainer creatorSection;

    /** Div about the resource creation date. */
    private final WebMarkupContainer createdSection;

    /** Div about the resource size. */
    private final WebMarkupContainer sizeSection;

    /** Div about the resource URI. */
    private final WebMarkupContainer resourceURISection;

    /** Div about the resource annotations. */
    private final WebMarkupContainer annotationsCntSection;

    /** Div about the resource stability measure. */
    private final WebMarkupContainer stabilitySection;

    /** Div about the resource relations. */
    private final WebMarkupContainer relationsSection;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param itemModel
     *            selected resource model
     */
    @SuppressWarnings("serial")
    public ItemInfoPanel(String id, final CompoundPropertyModel<AggregatedResource> itemModel) {
        super(id, itemModel);
        setOutputMarkupId(true);
        resourceURISection = new WebMarkupContainer("resourceURISection", new Model<>());
        add(resourceURISection);
        resourceURISection.add(new ExternalLink("resourceURI", itemModel.<String> bind("URI.toString"), itemModel
                .<URI> bind("URI")));
        creatorSection = new WebMarkupContainer("creatorSection", new Model<>());
        add(creatorSection);
        creatorSection.add(new CreatorsPanel("creator", new PropertyModel<List<Creator>>(itemModel, "creators")));
        createdSection = new WebMarkupContainer("createdSection", new Model<>());
        add(createdSection);
        createdSection.add(new Label("createdFormatted"));
        sizeSection = new WebMarkupContainer("sizeSection", new Model<>());
        add(sizeSection);
        sizeSection.add(new Label("sizeFormatted"));
        annotationsCntSection = new WebMarkupContainer("annotationsCntSection", new Model<>());
        add(annotationsCntSection);
        annotationsCntSection.add(new Label("annotations.size"));
        stabilitySection = new WebMarkupContainer("stabilitySection", new Model<>());
        stabilitySection.setOutputMarkupId(true);
        add(stabilitySection);
        stabilitySection.add(new Label("stability"));
        stabilitySection.add(new AjaxLink<String>("recalculateStability") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    PortalApplication app = (PortalApplication) getApplication();
                    double score = StabilityService.calculateStability(app.getStabilityEndpointURL().toURI(), itemModel
                            .getObject().getProvenanceTraceURI());
                    itemModel.getObject().setStability(score);
                    target.add(stabilitySection);
                } catch (Exception e) {
                    LOG.error(e);
                }
            }

        });

        relationsSection = new WebMarkupContainer("relationsSection", new Model<>());
        add(relationsSection);
        ListView<String> relationsGroup = new ListView<String>("relationTarget", new PropertyModel<List<String>>(
                itemModel, "relationKeys")) {

            @Override
            protected ListItem<String> newItem(int index, IModel<String> itemModel) {
                return new MyListItem(index, itemModel);
            }


            @Override
            protected void populateItem(final ListItem<String> item) {
                String key = item.getModelObject();
                item.add(new Label("relationType", key));

                final List<AggregatedResource> fullList = new ArrayList<>(itemModel.getObject().getRelations().get(key));
                if (fullList.size() <= 3) {
                    ((MyListItem) item).setList(fullList);
                } else {
                    ((MyListItem) item).setList(new ArrayList<>(fullList.subList(0, 2)));
                    ((MyListItem) item).getList().add(null);
                }

                final ListView<AggregatedResource> quickView = new ListView<AggregatedResource>("target",
                        new PropertyModel<List<AggregatedResource>>(item, "list")) {

                    @Override
                    protected void populateItem(final ListItem<AggregatedResource> item2) {
                        final AggregatedResource resource = item2.getModelObject();
                        item2.add(new WebMarkupContainer("separator").setVisible(item2.getIndex() > 0));
                        AjaxLink<String> link = new AjaxLink<String>("targetLink") {

                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                if (resource == null) {
                                    ((MyListItem) item).getList().remove(resource);
                                    ((MyListItem) item).getList().addAll(fullList);
                                    target.add(item);
                                }
                            }

                        };
                        item2.add(link);
                        if (resource != null) {
                            link.add(new Label("targetLabel", new PropertyModel<String>(resource, "name")));
                        } else {
                            link.add(new Label("targetLabel", "" + (fullList.size() - 2) + " more..."));
                        }
                    }
                };

                item.add(quickView);
                item.setOutputMarkupId(true);
            }

        };
        relationsSection.add(relationsGroup);
    }


    @Override
    protected void onConfigure() {
        AggregatedResource resource = (AggregatedResource) getDefaultModelObject();
        if (resource != null) {
            resourceURISection.setVisible(true);
            creatorSection.setVisible(!resource.getCreators().isEmpty());
            createdSection.setVisible(resource.getCreated() != null);
            sizeSection.setVisible(resource.getSizeFormatted() != null);
            stabilitySection.setVisible(resource.getStability() >= 0);
            annotationsCntSection.setVisible(true);
            relationsSection.setVisible(!resource.getRelations().isEmpty());
        } else {
            resourceURISection.setVisible(false);
            creatorSection.setVisible(false);
            createdSection.setVisible(false);
            sizeSection.setVisible(false);
            stabilitySection.setVisible(false);
            annotationsCntSection.setVisible(false);
            relationsSection.setVisible(false);
        }
    }


    /**
     * A relations list item, contains a list of relation targets.
     * 
     * @author piotrekhol
     */
    private class MyListItem extends ListItem<String> {

        /** id. */
        private static final long serialVersionUID = 7849773836371849967L;

        /** Item content. */
        private List<AggregatedResource> list;


        /**
         * Constructor.
         * 
         * @param index
         *            item index on the list
         * @param model
         *            a model containing the relation name
         */
        public MyListItem(int index, IModel<String> model) {
            super(index, model);
        }


        public List<AggregatedResource> getList() {
            return list;
        }


        public void setList(List<AggregatedResource> list) {
            this.list = list;
        }
    }

}
