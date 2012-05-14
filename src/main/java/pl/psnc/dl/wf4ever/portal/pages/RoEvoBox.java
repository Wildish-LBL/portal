/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.model.RoEvoNode;
import pl.psnc.dl.wf4ever.portal.services.RoEvoService;

/**
 * @author Piotr Ho¸ubowicz
 * 
 */
public class RoEvoBox
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;

	private enum Direction {
		HORIZONTAL, VERTICAL
	}


	@SuppressWarnings("serial")
	public RoEvoBox(String id, URI sparqlEndpointURI, URI researchObjectURI)
		throws IOException, URISyntaxException
	{
		super(id);

		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);

		final StringBuilder sb = new StringBuilder();

		List<RoEvoNode> nodes = new ArrayList<>(RoEvoService.describeSnapshot(sparqlEndpointURI, researchObjectURI));
		Collections.sort(nodes, new Comparator<RoEvoNode>() {

			@Override
			public int compare(RoEvoNode n1, RoEvoNode n2)
			{
				if (n1.getCreated() != null && n2.getCreated() != null) {
					if (n1.getCreated().isBefore(n2.getCreated())) {
						return -1;
					}
					if (n1.getCreated().isAfter(n2.getCreated())) {
						return 1;
					}
				}
				if (n2.getPreviousSnapshots().contains(n1)) {
					return -1;
				}
				if (n1.getPreviousSnapshots().contains(n2)) {
					return 1;
				}
				return 0;
			}
		});
		final int dist = 15;
		add(new ListView<RoEvoNode>("roEvoNode", nodes) {

			int liveX = 0;

			int liveY = 0;

			int snapX = 0;

			int snapY = 0 + dist;


			@Override
			protected void populateItem(ListItem<RoEvoNode> item)
			{
				item.add(new Label("identifier", new PropertyModel<>(item.getModelObject(), "identifier")));
				int x, y;
				switch (item.getModelObject().getEvoClass()) {
					case LIVE:
						x = liveX;
						y = liveY;
						liveX += dist;
						break;
					case SNAPSHOT:
						x = snapX;
						y = snapY;
						snapX += dist;
						break;
					default:
						x = 0;
						y = 0;
						break;
				}
				item.add(new AttributeModifier("style", "left: " + x + "em; top: " + y + "em;"));
				item.getModelObject().setComponent(item);
			}

		});

		for (RoEvoNode source : nodes) {
			for (RoEvoNode node : source.getItsLiveROs()) {
				sb.append(createConnection(source, node, null, Direction.HORIZONTAL));
			}

		}

		add(new Behavior() {

			@Override
			public void renderHead(Component component, IHeaderResponse response)
			{
				super.renderHead(component, response);
				response.renderOnLoadJavaScript(sb.toString());
			}
		});
	}


	protected String createConnection(RoEvoNode source, RoEvoNode target, String label, Direction direction)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("jsPlumb.connect({");
		sb.append("source: '" + source.getComponent().getMarkupId() + "',");
		sb.append("target: '" + target.getComponent().getMarkupId() + "',");
		switch (direction) {
			default:
			case HORIZONTAL:
				sb.append("anchors : [ 'LeftMiddle', 'RightMiddle' ],");
				break;
			case VERTICAL:
				sb.append("anchors : [ 'BottomCenter', 'TopCenter' ],");
				break;
		}
		sb.append("connector : 'Straight'});");
		return sb.toString();
	}
}
