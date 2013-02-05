package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import java.net.URI;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.Creator;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.Thing;

import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;

/**
 * Aggregated resource panel.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ROStatusBar extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(ResourceStatusBar.class);

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

    /** Div about the resource title. */
    private final WebMarkupContainer titleSection;

    /** Div about the resource description. */
    private final WebMarkupContainer descSection;

    /** Div about the RO evolution Class. */
    private final WebMarkupContainer evoClassSection;

    /** Main container for all labels. **/
    WebMarkupContainer mainContainer;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param itemModel
     *            selected resource model
     */
    public ROStatusBar(String id, final CompoundPropertyModel<Thing> itemModel) {
        super(id, itemModel);
        setOutputMarkupId(true);
        mainContainer = new WebMarkupContainer("main-container");
        resourceURISection = new WebMarkupContainer("resourceURISection", new Model<>());
        mainContainer.add(resourceURISection);
        resourceURISection.add(new ExternalLink("resourceURI", itemModel.<String> bind("uri.toString"), itemModel
                .<URI> bind("uri")));
        creatorSection = new WebMarkupContainer("creatorSection", new Model<>());
        mainContainer.add(creatorSection);
        creatorSection.add(new CreatorsPanel("creator", new PropertyModel<Set<Creator>>(itemModel, "creators")));
        createdSection = new WebMarkupContainer("createdSection", new Model<>());
        mainContainer.add(createdSection);
        createdSection.add(new Label("createdFormatted"));
        sizeSection = new WebMarkupContainer("sizeSection", new Model<>());
        mainContainer.add(sizeSection);
        sizeSection.add(new Label("sizeFormatted"));
        annotationsCntSection = new WebMarkupContainer("annotationsCntSection", new Model<>());
        annotationsCntSection.add(new Label("annotations.size"));
        mainContainer.add(annotationsCntSection);

        titleSection = new WebMarkupContainer("titleSection", new Model<>());
        titleSection.add(new Label("title"));
        descSection = new WebMarkupContainer("descSection", new Model<>());
        descSection.add(new Label("description"));
        evoClassSection = new WebMarkupContainer("evoClassSection", new Model<>());
        evoClassSection.add(new Label("evoType.toString"));
        mainContainer.add(titleSection);
        mainContainer.add(descSection);
        mainContainer.add(evoClassSection);

        add(mainContainer);
    }


    @Override
    protected void onConfigure() {
        Thing resource = (Thing) getDefaultModelObject();
        if (resource != null) {
            mainContainer.setVisible(true);
            resourceURISection.setVisible(true);
            //            creatorSection.setVisible(!resource.getCreators().isEmpty());
            creatorSection.setVisible(false);
            createdSection.setVisible(resource.getCreated() != null);
            //            sizeSection.setVisible(resource.getSizeFormatted() != null);
            sizeSection.setVisible(false);
            annotationsCntSection.setVisible(true);

            titleSection.setVisible(false);
            descSection.setVisible(false);
            evoClassSection.setVisible(false);

            if (resource instanceof ResearchObject) {
                ResearchObject ro = (ResearchObject) resource;
                if (ro.getTitle() != null && !ro.getTitle().equals("")) {
                    titleSection.setVisible(true);
                }
                if (ro.getDescription() != null && !ro.getDescription().equals("")) {
                    descSection.setVisible(true);
                }
                if (ro.getEvoType() != null) {
                    evoClassSection.setVisible(true);
                }
            }
        } else {
            mainContainer.setVisible(false);
        }
    }
}
