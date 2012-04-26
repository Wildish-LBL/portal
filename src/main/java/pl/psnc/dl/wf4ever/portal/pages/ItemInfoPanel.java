/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.PortalApplication;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.services.StabilityService;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class ItemInfoPanel
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;

	private static final Logger log = Logger.getLogger(ItemInfoPanel.class);

	private final WebMarkupContainer downloadURISection;

	private final WebMarkupContainer creatorSection;

	private final WebMarkupContainer createdSection;

	private final WebMarkupContainer sizeSection;

	private final WebMarkupContainer resourceURISection;

	private final WebMarkupContainer annotationsCntSection;

	private final WebMarkupContainer stabilitySection;

	private final WebMarkupContainer relationsSection;


	@SuppressWarnings("serial")
	public ItemInfoPanel(String id, final CompoundPropertyModel<AggregatedResource> itemModel)
	{
		super(id, itemModel);
		setOutputMarkupId(true);
		resourceURISection = new WebMarkupContainer("resourceURISection", new Model<>());
		add(resourceURISection);
		resourceURISection.add(new ExternalLink("resourceURI", itemModel.<String> bind("URI.toString"), itemModel
				.<URI> bind("URI")));
		downloadURISection = new WebMarkupContainer("downloadURISection", new Model<>());
		add(downloadURISection);
		downloadURISection.add(new ExternalLink("downloadURI", itemModel.<String> bind("downloadURI.toString"),
				itemModel.<URI> bind("downloadURI")));
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
			public void onClick(AjaxRequestTarget target)
			{
				try {
					PortalApplication app = (PortalApplication) getApplication();
					double score = StabilityService.calculateStability(app.getStabilityEndpointURL().toURI(), itemModel
							.getObject().getProvenanceTraceURI());
					itemModel.getObject().setStability(score);
					target.add(stabilitySection);
				}
				catch (Exception e) {
					log.error(e);
				}
			}

		});

		relationsSection = new WebMarkupContainer("relationsSection", new Model<>());
		add(relationsSection);
		ListView<Entry<String, AggregatedResource>> relationsGroup = new ListView<Entry<String, AggregatedResource>>(
				"relationTarget", new PropertyModel<List<Entry<String, AggregatedResource>>>(itemModel,
						"relationsEntries")) {

			@Override
			protected void populateItem(ListItem<Entry<String, AggregatedResource>> item)
			{
				Entry<String, AggregatedResource> entry = item.getModelObject();
				item.add(new Label("relationType", entry.getKey()));
				AjaxLink<String> link = new AjaxLink<String>("targetLink") {

					@Override
					public void onClick(AjaxRequestTarget target)
					{
						// TODO Auto-generated method stub

					}

				};
				item.add(link);
				link.add(new Label("targetLabel", new PropertyModel<String>(entry.getValue(), "name")));
			}

		};
		relationsSection.add(relationsGroup);
	}


	@Override
	protected void onConfigure()
	{
		AggregatedResource resource = (AggregatedResource) getDefaultModelObject();
		if (resource != null) {
			resourceURISection.setVisible(true);
			downloadURISection.setVisible(resource.getDownloadURI() != null);
			creatorSection.setVisible(!resource.getCreators().isEmpty());
			createdSection.setVisible(resource.getCreated() != null);
			sizeSection.setVisible(resource.getSizeFormatted() != null);
			stabilitySection.setVisible(resource.getStability() >= 0);
			annotationsCntSection.setVisible(true);
			relationsSection.setVisible(!resource.getRelations().isEmpty());
		}
		else {
			resourceURISection.setVisible(false);
			downloadURISection.setVisible(false);
			creatorSection.setVisible(false);
			createdSection.setVisible(false);
			sizeSection.setVisible(false);
			stabilitySection.setVisible(false);
			annotationsCntSection.setVisible(false);
			relationsSection.setVisible(false);
		}
	}

}
