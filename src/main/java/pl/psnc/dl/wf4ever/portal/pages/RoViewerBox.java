package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.crypt.Base64;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.RoTree;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

@SuppressWarnings("serial")
class RoViewerBox
	extends WebMarkupContainer
{

	private static final Logger log = Logger.getLogger(RoViewerBox.class);

	/**
	 * 
	 */
	private final RoPage roPage;

	RoTree conceptualTree;

	private RoTree physicalTree;

	private WebMarkupContainer interactiveView;

	Panel infoPanel;

	private final ItemInfoPanel itemInfo;

	private final InfoPanel resourceGroupInfo;

	final WebMarkupContainer actionButtons;

	private Fragment conceptualTreeLoading;

	private Fragment physicalTreeLoading;

	private Fragment interactiveViewLoading;

	final AbstractDefaultAjaxBehavior interactiveViewCallback;


	public RoViewerBox(final RoPage roPage, final CompoundPropertyModel<AggregatedResource> itemModel,
			IModel< ? extends TreeModel> conceptualTreeModel, PropertyModel<TreeModel> physicalTreeModel,
			String tempRoTreeId)
	{
		super("roViewerBox", itemModel);
		this.roPage = roPage;
		setOutputMarkupId(true);
		add(new Label("title", this.roPage.roURI.toString()));

		final CompoundPropertyModel<ResourceGroup> resourceGroupModel = new CompoundPropertyModel<ResourceGroup>(
				(ResourceGroup) null);
		itemInfo = new ItemInfoPanel("itemInfo", itemModel);
		resourceGroupInfo = new InfoPanel("itemInfo", resourceGroupModel);
		infoPanel = itemInfo;
		add(infoPanel);

		conceptualTreeLoading = new Fragment("treeTable", tempRoTreeId, this.roPage);
		conceptualTree = new RoTree("treeTable", conceptualTreeModel) {

			@Override
			protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node)
			{
				Object object = ((DefaultMutableTreeNode) node).getUserObject();
				if (object instanceof AggregatedResource) {
					setInfoPanel(itemModel, (AggregatedResource) object);
				}
				else if (object instanceof ResourceGroup) {
					setInfoPanel(resourceGroupModel, (ResourceGroup) object);
				}
				RoViewerBox.this.roPage.annotatingBox.selectedStatements.clear();
				target.add(actionButtons);
				target.add(infoPanel);
				target.add(RoViewerBox.this.roPage.annotatingBox);
			}

		};
		add(conceptualTreeLoading);

		physicalTreeLoading = new Fragment("physicalTreeTable", tempRoTreeId, this.roPage);
		physicalTree = new RoTree("physicalTreeTable", physicalTreeModel) {

			@Override
			protected void onNodeLinkClicked(AjaxRequestTarget target, TreeNode node)
			{
				Object object = ((DefaultMutableTreeNode) node).getUserObject();
				if (object instanceof AggregatedResource) {
					setInfoPanel(itemModel, (AggregatedResource) object);
				}
				RoViewerBox.this.roPage.annotatingBox.selectedStatements.clear();
				target.add(actionButtons);
				target.add(infoPanel);
				target.add(RoViewerBox.this.roPage.annotatingBox);
			}

		};
		add(physicalTreeLoading);

		interactiveViewLoading = new Fragment("interactiveView", tempRoTreeId, this.roPage);
		interactiveView = new WebMarkupContainer("interactiveView");
		add(interactiveViewLoading);

		Form< ? > roForm = new Form<Void>("roForm");
		add(roForm);

		actionButtons = new WebMarkupContainer("actionButtons");
		actionButtons.setOutputMarkupId(true);
		roForm.add(actionButtons);

		MyAjaxButton addResource = new MyAjaxButton("addResource", roForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#upload-resource-modal').modal('show')");
			}


			@Override
			public boolean isEnabled()
			{
				return super.isEnabled() && RoViewerBox.this.roPage.canEdit;
			}
		};
		actionButtons.add(addResource);

		MyAjaxButton deleteResource = new MyAjaxButton("deleteResource", roForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) conceptualTree.getTreeState()
						.getSelectedNodes().iterator().next()).getUserObject();
				try {
					ROSRService.deleteResource(res.getURI(), MySession.get().getdLibraAccessToken());
					RoViewerBox.this.roPage.getConceptualResourcesTree().removeAggregatedResource(res);
					conceptualTree.invalidateAll();
					target.add(roPage);
				}
				catch (Exception e) {
					error(e);
				}
			}


			@Override
			public boolean isEnabled()
			{
				if (super.isEnabled() && RoViewerBox.this.roPage.canEdit
						&& !conceptualTree.getTreeState().getSelectedNodes().isEmpty()) {
					Object object = ((DefaultMutableTreeNode) conceptualTree.getTreeState().getSelectedNodes()
							.iterator().next()).getUserObject();
					return object instanceof AggregatedResource
							&& !((AggregatedResource) object).getURI().equals(RoViewerBox.this.roPage.roURI);
				}
				return false;
			}
		};
		actionButtons.add(deleteResource);

		AjaxButton downloadMetadata = new MyAjaxButton("downloadMetadata", roForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#download-metadata-modal').modal('show')");
			}

		};
		actionButtons.add(downloadMetadata);

		interactiveViewCallback = new AbstractDefaultAjaxBehavior() {

			protected void respond(final AjaxRequestTarget target)
			{
				String nodeId = RequestCycle.get().getRequest().getQueryParameters().getParameterValue("id").toString();
				try {
					URI resourceURI = new URI(new String(Base64.decodeBase64(nodeId)));
					if (RoViewerBox.this.roPage.resources.containsKey(resourceURI)) {
						setInfoPanel(itemModel, RoViewerBox.this.roPage.resources.get(resourceURI));
						RoViewerBox.this.roPage.annotatingBox.selectedStatements.clear();
						target.add(actionButtons);
						target.add(infoPanel);
						target.add(RoViewerBox.this.roPage.annotatingBox);
					}
				}
				catch (URISyntaxException e) {
					log.error("Could not parse node id", e);
				}
			}
		};
		add(interactiveViewCallback);
	}


	public CharSequence getInteractiveViewCallbackUrl()
	{
		return interactiveViewCallback.getCallbackUrl();
	}


	private void setInfoPanel(final CompoundPropertyModel<AggregatedResource> itemModel, AggregatedResource res)
	{
		itemModel.setObject(res);
		if (infoPanel != itemInfo) {
			infoPanel.replaceWith(itemInfo);
			infoPanel = itemInfo;
		}
	}


	private void setInfoPanel(final CompoundPropertyModel<ResourceGroup> resourceGroupModel, ResourceGroup res)
	{
		resourceGroupModel.setObject(res);
		if (infoPanel != resourceGroupInfo) {
			infoPanel.replaceWith(resourceGroupInfo);
			infoPanel = resourceGroupInfo;
		}
	}


	public void onRoTreeLoaded()
	{
		conceptualTreeLoading.replaceWith(conceptualTree);
		physicalTreeLoading.replaceWith(physicalTree);
		interactiveViewLoading.replaceWith(interactiveView);
	}
}