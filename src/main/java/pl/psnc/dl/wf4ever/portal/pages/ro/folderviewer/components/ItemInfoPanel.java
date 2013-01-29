package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components;

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
import org.purl.wf4ever.rosrs.client.Thing;

import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;

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
    @SuppressWarnings("unused")
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


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param itemModel
     *            selected resource model
     */
    public ItemInfoPanel(String id, final CompoundPropertyModel<Thing> itemModel) {
        super(id, itemModel);
        setOutputMarkupId(true);
        resourceURISection = new WebMarkupContainer("resourceURISection", new Model<>());
        add(resourceURISection);
        resourceURISection.add(new ExternalLink("resourceURI", itemModel.<String> bind("uri.toString"), itemModel
                .<URI> bind("uri")));
        creatorSection = new WebMarkupContainer("creatorSection", new Model<>());
        add(creatorSection);
        creatorSection.add(new CreatorsPanel("creator", new PropertyModel<Set<Creator>>(itemModel, "creators")));
        createdSection = new WebMarkupContainer("createdSection", new Model<>());
        add(createdSection);
        createdSection.add(new Label("createdFormatted"));
        sizeSection = new WebMarkupContainer("sizeSection", new Model<>());
        add(sizeSection);
        sizeSection.add(new Label("sizeFormatted"));
        annotationsCntSection = new WebMarkupContainer("annotationsCntSection", new Model<>());
        add(annotationsCntSection);
        annotationsCntSection.add(new Label("annotations.size"));
    }


    @Override
    protected void onConfigure() {
        Thing resource = (Thing) getDefaultModelObject();
        if (resource != null) {
            resourceURISection.setVisible(true);
            //            creatorSection.setVisible(!resource.getCreators().isEmpty());
            creatorSection.setVisible(false);
            createdSection.setVisible(resource.getCreated() != null);
            //            sizeSection.setVisible(resource.getSizeFormatted() != null);
            sizeSection.setVisible(false);
            annotationsCntSection.setVisible(true);
        } else {
            resourceURISection.setVisible(false);
            creatorSection.setVisible(false);
            createdSection.setVisible(false);
            sizeSection.setVisible(false);
            annotationsCntSection.setVisible(false);
        }
    }

}
