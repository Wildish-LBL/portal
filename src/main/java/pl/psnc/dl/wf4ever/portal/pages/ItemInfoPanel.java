/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

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


	public ItemInfoPanel(String id, CompoundPropertyModel<AggregatedResource> itemModel)
	{
		super(id, itemModel);
		setOutputMarkupId(true);
		add(new ExternalLink("resourceURI", itemModel.<String> bind("URI.toString"), itemModel.<URI> bind("URI")));
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
		add(new Label("annotations.size"));
	}


	@Override
	protected void onConfigure()
	{
		AggregatedResource resource = (AggregatedResource) getDefaultModelObject();
		downloadURISection.setVisible(resource.getDownloadURI() != null);
		creatorSection.setVisible(resource.getCreator() != null);
		createdSection.setVisible(resource.getCreated() != null);
		sizeSection.setVisible(resource.getSizeFormatted() != null);
	}

}
