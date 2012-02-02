/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class ItemInfoPanel
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;


	public ItemInfoPanel(String id, CompoundPropertyModel<AggregatedResource> itemModel)
	{
		super(id, itemModel);
		setOutputMarkupId(true);
		add(new ExternalLink("resourceURI", itemModel.<String> bind("URI.toString"), itemModel.<URI> bind("URI")));
		add(new ExternalLink("downloadURI", itemModel.<String> bind("downloadURI.toString"),
				itemModel.<URI> bind("downloadURI")));
		add(new Label("creator"));
		add(new Label("createdFormatted"));
		add(new Label("sizeFormatted"));
		add(new Label("annotations.size"));
	}

}
