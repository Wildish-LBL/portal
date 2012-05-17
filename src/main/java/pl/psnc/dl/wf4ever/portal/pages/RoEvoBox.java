/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.model.RoEvoNode;
import pl.psnc.dl.wf4ever.portal.services.RoEvoService;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class RoEvoBox
	extends Panel
{

	private static final long serialVersionUID = -3775797988389365540L;


	@SuppressWarnings("serial")
	public RoEvoBox(String id, URI sparqlEndpointURI, final URI researchObjectURI)
		throws IOException, URISyntaxException
	{
		super(id);

		setOutputMarkupId(true);
		setOutputMarkupPlaceholderTag(true);

		Collection<RoEvoNode> nodes = RoEvoService.describeSnapshot(sparqlEndpointURI, researchObjectURI);
		List<RoEvoNode> preorder = new ArrayList<>();
		final List<RoEvoNode> postorder = new ArrayList<>();
		for (RoEvoNode node : nodes) {
			if (!preorder.contains(node)) {
				visit(node, preorder, postorder);
			}
		}
		final int dx = 15;
		final int dy = 12;
		add(new ListView<RoEvoNode>("roEvoNode", postorder) {

			private int liveX, liveY, snapX, snapY;


			@Override
			protected void onConfigure()
			{
				super.onConfigure();
				liveX = 0;
				liveY = 0;
				snapX = 0;
				snapY = 0 + dy;
			}


			@Override
			protected void populateItem(ListItem<RoEvoNode> item)
			{
				int x, y;
				StringBuilder cssClasses = new StringBuilder();
				item.add(new Label("labelOrIdentifier", new PropertyModel<>(item.getModelObject(), "labelOrIdentifier")));
				switch (item.getModelObject().getEvoClass()) {
					case LIVE:
						x = liveX;
						y = liveY;
						liveX += dx;
						cssClasses.append(" live");
						break;
					case ARCHIVED:
						y = snapY + dy;
						x = snapX;
						snapX += dx;
						cssClasses.append(" archived");
						break;
					case SNAPSHOT:
						x = snapX;
						y = snapY;
						snapX += dx;
						cssClasses.append(" snapshot");
						break;
					default:
						x = 0;
						y = 0;
						break;
				}
				if (researchObjectURI.equals(item.getModelObject().getUri())) {
					cssClasses.append(" active");
				}
				item.add(new AttributeAppender("style", "left: " + x + "em; top: " + y + "em;"));
				item.add(new AttributeAppender("class", cssClasses.toString()));
				item.add(new WebMarkupContainer("roMark").setVisible(item.getModelObject().isResearchObject()));

				item.getModelObject().setComponent(item);
				item.setOutputMarkupId(true);
			}

		});

		add(new Behavior() {

			@Override
			public void renderHead(Component component, IHeaderResponse response)
			{
				super.renderHead(component, response);
				final StringBuilder sb = new StringBuilder();
				sb.append("jsPlumb.ready(function() {");

				for (RoEvoNode source : postorder) {
					for (RoEvoNode node : source.getItsLiveROs()) {
						sb.append(createConnection(source, node, "Has live RO"));
					}
					for (RoEvoNode node : source.getPreviousSnapshots()) {
						sb.append(createConnection(source, node, "Previous snapshot"));
					}

				}

				sb.append("});");

				response.renderOnLoadJavaScript(sb.toString());
			}
		});
	}


	private void visit(RoEvoNode node, List<RoEvoNode> preorder, List<RoEvoNode> postorder)
	{
		preorder.add(node);
		for (RoEvoNode n : node.getPreviousSnapshots()) {
			if (!preorder.contains(n)) {
				visit(n, preorder, postorder);
			}
		}
		for (RoEvoNode n : node.getItsLiveROs()) {
			if (!preorder.contains(n)) {
				visit(n, preorder, postorder);
			}
		}
		postorder.add(node);
	}


	protected String createConnection(RoEvoNode source, RoEvoNode target, String label)
	{

		StringBuilder sb = new StringBuilder();
		String connId = "conn" + Math.abs(new Random().nextInt());
		sb.append("var " + connId + " = jsPlumb.connect({");
		sb.append("source: '" + source.getComponent().getMarkupId() + "',");
		sb.append("target: '" + target.getComponent().getMarkupId() + "',");
		sb.append("overlays:[ [ 'Label', { label:'" + label + "', id: 'label', cssClass : 'evolabel' } ] ]");
		sb.append("});");
		sb.append(connId + ".bind('mouseenter', function (c) { c.showOverlay('label'); });");
		sb.append(connId + ".bind('mouseexit', function (c) { c.hideOverlay('label'); });");
		sb.append(connId + ".hideOverlay('label');");
		return sb.toString();
	}
}
