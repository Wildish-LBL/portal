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

		WebMarkupContainer live = new WebMarkupContainer("live");
		add(live);
		WebMarkupContainer snapshots = new WebMarkupContainer("snapshots");
		add(snapshots);
		WebMarkupContainer archived = new WebMarkupContainer("archived");
		add(archived);

		Collection<RoEvoNode> nodes = RoEvoService.describeSnapshot(sparqlEndpointURI, researchObjectURI);
		List<RoEvoNode> preorder = new ArrayList<>();
		final List<RoEvoNode> postorder = new ArrayList<>();
		for (RoEvoNode node : nodes) {
			if (!preorder.contains(node)) {
				visit(node, preorder, postorder);
			}
		}
		final int dx = 150, ox = 20;
		List<RoEvoNode> liveNodes = new ArrayList<>();
		List<RoEvoNode> snapshotNodes = new ArrayList<>();
		List<RoEvoNode> archivedNodes = new ArrayList<>();
		for (RoEvoNode node : nodes) {
			switch (node.getEvoClass()) {
				case LIVE:
					liveNodes.add(node);
					break;
				case SNAPSHOT:
					snapshotNodes.add(node);
					break;
				case ARCHIVED:
					archivedNodes.add(node);
					break;
				default:
					snapshotNodes.add(node);
					break;
			}
		}
		if (!liveNodes.isEmpty()) {
			live.add(new ListView<RoEvoNode>("liveNodes", liveNodes) {

				@Override
				protected void populateItem(ListItem<RoEvoNode> item)
				{
					populateRoEvoNode(item, researchObjectURI, ox + dx * postorder.indexOf(item.getModelObject()));
				}

			});
		}
		else {
			live.setVisible(false);
		}
		if (!snapshotNodes.isEmpty()) {
			snapshots.add(new ListView<RoEvoNode>("snapshotNodes", snapshotNodes) {

				@Override
				protected void populateItem(ListItem<RoEvoNode> item)
				{
					populateRoEvoNode(item, researchObjectURI, ox + dx * postorder.indexOf(item.getModelObject()));
				}

			});
		}
		else {
			snapshots.setVisible(false);
		}
		if (!archivedNodes.isEmpty()) {
			archived.add(new ListView<RoEvoNode>("archivedNodes", archivedNodes) {

				@Override
				protected void populateItem(ListItem<RoEvoNode> item)
				{
					populateRoEvoNode(item, researchObjectURI, ox + dx * postorder.indexOf(item.getModelObject()));
				}

			});
		}
		else {
			archived.setVisible(false);
		}

		add(new Behavior() {

			@Override
			public void renderHead(Component component, IHeaderResponse response)
			{
				super.renderHead(component, response);
				final StringBuilder sb = new StringBuilder();
				sb.append("$('#evoroot').on('firstShow', function() {");

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


	private void populateRoEvoNode(ListItem<RoEvoNode> item, final URI researchObjectURI, int x)
	{
		RoEvoNode node = item.getModelObject();
		item.add(new Label("labelOrIdentifier", new PropertyModel<>(node, "labelOrIdentifier")));
		StringBuilder cssClasses = new StringBuilder();
		if (researchObjectURI.equals(item.getModelObject().getUri())) {
			cssClasses.append(" active");
		}
		item.add(new AttributeAppender("style", "left: " + x + "px;"));
		item.add(new AttributeAppender("class", cssClasses.toString()));
		item.add(new WebMarkupContainer("roMark").setVisible(node.isResearchObject()));
		node.setComponent(item);
		item.setOutputMarkupId(true);
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
