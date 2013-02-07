/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import pl.psnc.dl.wf4ever.portal.model.RoEvoNode;

/**
 * A panel for displaying the RO evolution surroundings.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class RoEvoBox extends Panel {

    /** id. */
    private static final long serialVersionUID = -3775797988389365540L;

    /** Logger. */
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(RoEvoBox.class);

    private final Map<ResearchObject, RoEvoNode> allNodes;

    /** next unassigned index. */
    private static int nextIndex;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param sparqlEndpointURI
     *            RODL SPARQL endpoint URI
     * @param researchObjectURI
     *            RO URI
     * @throws IOException
     *             when there are problems with connecting to the SPARQL endpoint
     * @throws URISyntaxException
     *             when data from the SPARQL endpoint contain invalid URIs
     */
    @SuppressWarnings("serial")
    public RoEvoBox(String id, URI sparqlEndpointURI, final ResearchObject researchObject)
            throws IOException {
        super(id);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        WebMarkupContainer live = new WebMarkupContainer("live");
        add(live);
        WebMarkupContainer snapshots = new WebMarkupContainer("snapshots");
        add(snapshots);
        WebMarkupContainer archived = new WebMarkupContainer("archived");
        add(archived);

        List<RoEvoNode> liveNodes = new ArrayList<>();
        List<RoEvoNode> snapshotNodes = new ArrayList<>();
        List<RoEvoNode> archivedNodes = new ArrayList<>();
        allNodes = new HashMap<>();

        List<ResearchObject> preorder = new ArrayList<>();
        List<ResearchObject> postorder = new ArrayList<>();

        researchObject.loadEvolutionInformation();
        nextIndex = 0;
        switch (researchObject.getEvoType()) {
            case LIVE:
                Set<ResearchObject> snapshotsAndArchives = new HashSet<>();
                snapshotsAndArchives.addAll(researchObject.getSnapshots());
                snapshotsAndArchives.addAll(researchObject.getArchives());
                for (ResearchObject ro : snapshotsAndArchives) {
                    if (!preorder.contains(ro)) {
                        visit(ro, preorder, postorder);
                    }
                }
                addNode(liveNodes, researchObject);
                for (ResearchObject ro : postorder) {
                    switch (ro.getEvoType()) {
                        case SNAPSHOT:
                            addNode(snapshotNodes, ro);
                            break;
                        case ARCHIVE:
                            addNode(archivedNodes, ro);
                        default:
                    }
                }
                break;
            default:
                break;
        }

        final int dx = 120, ox = 20;
        if (!liveNodes.isEmpty()) {
            live.add(new ListView<RoEvoNode>("liveNodes", liveNodes) {

                @Override
                protected void populateItem(ListItem<RoEvoNode> item) {
                    populateRoEvoNode(item, researchObject, ox + dx * item.getModelObject().getIndex());
                }

            });
        } else {
            live.setVisible(false);
        }
        if (!snapshotNodes.isEmpty()) {
            snapshots.add(new ListView<RoEvoNode>("snapshotNodes", snapshotNodes) {

                @Override
                protected void populateItem(ListItem<RoEvoNode> item) {
                    populateRoEvoNode(item, researchObject, ox + dx * item.getModelObject().getIndex());
                }

            });
        } else {
            snapshots.setVisible(false);
        }
        if (!archivedNodes.isEmpty()) {
            archived.add(new ListView<RoEvoNode>("archivedNodes", archivedNodes) {

                @Override
                protected void populateItem(ListItem<RoEvoNode> item) {
                    populateRoEvoNode(item, researchObject, ox + dx * item.getModelObject().getIndex());
                }

            });
        } else {
            archived.setVisible(false);
        }

        add(new Behavior() {

            @Override
            public void renderHead(Component component, IHeaderResponse response) {
                super.renderHead(component, response);
                response.renderOnLoadJavaScript(getDrawJavaScript());
            }
        });
    }


    private void addNode(List<RoEvoNode> nodes, ResearchObject ro) {
        RoEvoNode node = allNodes.containsKey(ro) ? allNodes.get(ro) : new RoEvoNode(ro);
        allNodes.put(ro, node);
        nodes.add(node);
        node.setIndex(nextIndex++);
    }


    private void visit(ResearchObject ro, List<ResearchObject> preorder, List<ResearchObject> postorder) {
        preorder.add(ro);
        ro.loadEvolutionInformation();
        if (ro.getPreviousSnapshot() != null && !preorder.contains(ro.getPreviousSnapshot())) {
            visit(ro.getPreviousSnapshot(), preorder, postorder);
        }
        postorder.add(ro);
    }


    /**
     * Create the RO node for the visualization.
     * 
     * @param item
     *            list item containing the node
     * @param researchObjectURI
     *            RO URI
     * @param x
     *            the horizontal position of the node, in px
     */
    private void populateRoEvoNode(ListItem<RoEvoNode> item, final ResearchObject researchObject, int x) {
        RoEvoNode node = item.getModelObject();
        item.add(new ExternalLink("labelOrIdentifier", new PropertyModel<String>(node, "researchObject.uri.toString"),
                new PropertyModel<>(node, "labelOrIdentifier")));
        StringBuilder cssClasses = new StringBuilder();
        if (researchObject.equals(item.getModelObject().getResearchObject())) {
            cssClasses.append(" active");
        }
        item.add(new AttributeAppender("style", "left: " + x + "px;"));
        item.add(new AttributeAppender("class", cssClasses.toString()));
        item.add(new AttributeAppender("rel", "popover"));
        item.add(new AttributeAppender("data-content", node.getResearchObject().getUri()));
        if (node.isResearchObject()) {
            item.add(new AttributeAppender("data-original-title", "Research Object"));
        } else {
            item.add(new AttributeAppender("data-original-title", "An external resource"));
        }
        item.add(new WebMarkupContainer("roMark").setVisible(node.isResearchObject()));
        node.setComponent(item);
        item.setOutputMarkupId(true);
    }


    /**
     * Create a JavaScript code that connects 2 nodes.
     * 
     * @param source
     *            source node
     * @param target
     *            target node
     * @param label
     *            connection label
     * @return JavaScript code
     */
    protected String createConnection(RoEvoNode source, RoEvoNode target, String label) {
        StringBuilder sb = new StringBuilder();
        String connId = "conn" + Math.abs(new Random().nextInt());
        sb.append("var " + connId + " = instance.connect({");
        sb.append("source: '" + source.getComponent().getMarkupId() + "',");
        sb.append("target: '" + target.getComponent().getMarkupId() + "',");
        sb.append("overlays:[ [ 'Label', { label:'" + label + "', id: 'label', cssClass : 'evolabel' } ] ]");
        sb.append("});");
        sb.append(connId + ".bind('mouseenter', function (c) { c.showOverlay('label'); });");
        sb.append(connId + ".bind('mouseexit', function (c) { c.hideOverlay('label'); });");
        sb.append(connId + ".hideOverlay('label');");
        return sb.toString();
    }


    private String getDrawJavaScript() {
        final StringBuilder sb = new StringBuilder();
        sb.append("jsPlumb.ready(function() {");
        sb.append("initRoEvo(jsPlumb);");
        sb.append("var instance = jsPlumb.getInstance();");
        sb.append("initRoEvo(instance);");
        for (RoEvoNode node : allNodes.values()) {
            if (node.getResearchObject().getLiveRO() != null) {
                RoEvoNode live = allNodes.get(node.getResearchObject().getLiveRO());
                sb.append(createConnection(node, live, "Has live RO"));
            }
            if (node.getResearchObject().getPreviousSnapshot() != null) {
                RoEvoNode snapshot = allNodes.get(node.getResearchObject().getPreviousSnapshot());
                sb.append(createConnection(node, snapshot, "Previous snapshot"));
            }
        }
        sb.append("});");
        return sb.toString();
    }

}
