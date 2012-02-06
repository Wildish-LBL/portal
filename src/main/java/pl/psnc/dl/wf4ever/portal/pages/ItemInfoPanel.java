/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class ItemInfoPanel
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;

	private final WebMarkupContainer downloadURISection;

	private final WebMarkupContainer creatorSection;

	private final WebMarkupContainer createdSection;

	private final WebMarkupContainer sizeSection;

	private final WebMarkupContainer resourceURISection;

	private final WebMarkupContainer annotationsCntSection;


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
		creatorSection.add(new Label("creator"));
		createdSection = new WebMarkupContainer("createdSection", new Model<>());
		add(createdSection);
		createdSection.add(new Label("createdFormatted"));
		sizeSection = new WebMarkupContainer("sizeSection", new Model<>());
		add(sizeSection);
		sizeSection.add(new Label("sizeFormatted"));
		annotationsCntSection = new WebMarkupContainer("annotationsCntSection", new Model<>());
		add(annotationsCntSection);
		annotationsCntSection.add(new Label("annotations.size"));

		ListView<String> relationsGroup = new ListView<String>("relationsGroup", new PropertyModel<List<String>>(
				itemModel, "relationsKeyList")) {

			@Override
			protected void populateItem(ListItem<String> item)
			{
				String key = item.getModelObject();
				item.add(new Label("relationType", key));
				List<AggregatedResource> targets = new ArrayList<AggregatedResource>(itemModel.getObject()
						.getRelations().get(key));
				ListView<AggregatedResource> targetList = new ListView<AggregatedResource>("relationTarget", targets) {

					@Override
					protected void populateItem(ListItem<AggregatedResource> item)
					{
						AggregatedResource resource = item.getModelObject();
						AjaxLink<String> link = new AjaxLink<String>("targetLink") {

							@Override
							public void onClick(AjaxRequestTarget target)
							{
								// TODO Auto-generated method stub

							}

						};
						item.add(link);
						link.add(new Label("targetLabel", new PropertyModel<String>(resource, "name")));
					}

				};
				item.add(targetList);
			}

		};
		add(relationsGroup);
	}


	@Override
	protected void onConfigure()
	{
		AggregatedResource resource = (AggregatedResource) getDefaultModelObject();
		if (resource != null) {
			resourceURISection.setVisible(true);
			downloadURISection.setVisible(resource.getDownloadURI() != null);
			creatorSection.setVisible(resource.getCreator() != null);
			createdSection.setVisible(resource.getCreated() != null);
			sizeSection.setVisible(resource.getSizeFormatted() != null);
			annotationsCntSection.setVisible(true);
		}
		else {
			resourceURISection.setVisible(false);
			downloadURISection.setVisible(false);
			creatorSection.setVisible(false);
			createdSection.setVisible(false);
			sizeSection.setVisible(false);
			annotationsCntSection.setVisible(false);
		}
	}

}
