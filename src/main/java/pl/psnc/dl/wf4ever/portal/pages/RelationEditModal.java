package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.RoTree;
import pl.psnc.dl.wf4ever.portal.pages.util.URIConverter;

class RelationEditModal
	extends Panel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -805443481947725257L;

	private final TextField<String> subjectURI;

	private final TextField<URI> relationURI;

	private URI selectedRelation;

	private String title;

	private Form<Statement> form;

	private RoTree tree;

	private Fragment treeLoading;

	private RoPage roPage;


	@SuppressWarnings("serial")
	public RelationEditModal(String id, final RoPage roPage, CompoundPropertyModel<Statement> model, String tempRoTreeId)
	{
		super(id, model);
		this.roPage = roPage;
		setOutputMarkupId(true);
		form = new Form<>("relEditForm", model);
		add(form);

		form.add(new Label("title", new PropertyModel<String>(this, "title")));

		subjectURI = new TextField<String>("subjectURI", new PropertyModel<String>(this, "subjectURIShort"));
		subjectURI.setEnabled(false);
		form.add(subjectURI);

		List<URI> choices = Arrays.asList(RoFactory.defaultRelations);
		DropDownChoice<URI> relations = new DropDownChoice<URI>("relationURI", new PropertyModel<URI>(this,
				"selectedRelation"), choices);
		relations.setNullValid(true);
		form.add(relations);

		final WebMarkupContainer relationURIDiv = new WebMarkupContainer("customRelationURIDiv");
		form.add(relationURIDiv);

		relationURI = new TextField<URI>("customRelationURI", new PropertyModel<URI>(this, "customRelation"), URI.class) {

			@SuppressWarnings("unchecked")
			@Override
			public <C> IConverter<C> getConverter(Class<C> type)
			{
				return (IConverter<C>) new URIConverter();
			}
		};
		relationURIDiv.add(relationURI);

		tree = new RoTree("objectTree", new PropertyModel<TreeModel>(roPage, "aggregatedResourcesTree"));
		treeLoading = new Fragment("objectTree", tempRoTreeId, roPage);
		form.add(treeLoading);

		form.add(new IFormValidator() {

			@Override
			public void validate(Form< ? > form)
			{
				if (tree.getTreeState().getSelectedNodes().isEmpty()) {
					form.error("You have to select one resource");
					return;
				}
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getTreeState().getSelectedNodes()
						.iterator().next();
				if (!(node.getUserObject() instanceof AggregatedResource)) {
					form.error("You must select a resource");
				}
			}


			@Override
			public FormComponent< ? >[] getDependentFormComponents()
			{
				return null;
			}
		});

		form.add(new MyAjaxButton("save", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				Statement statement = RelationEditModal.this.getModelObject();
				AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) tree.getTreeState()
						.getSelectedNodes().iterator().next()).getUserObject();
				statement.setObjectURI(res.getURI());

				try {
					if (statement.getAnnotation() == null) {
						roPage.onStatementAdd(statement);
					}
					else {
						roPage.onStatementEdit(statement);
					}
					roPage.onRelationAddedEdited(target);
					target.add(form);
					target.appendJavaScript("$('#edit-rel-modal').modal('hide')");
				}
				catch (Exception e) {
					error("" + e.getMessage());
				}
			}
		});
		form.add(new MyAjaxButton("cancel", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#edit-rel-modal').modal('hide')");
			}
		}.setDefaultFormProcessing(false));
	}


	public void onRoTreeLoaded()
	{
		treeLoading.replaceWith(tree);
	}


	/**
	 * @return the selectedProperty
	 */
	public URI getSelectedRelation()
	{
		if (selectedRelation == null && getModelObject() != null)
			return getModelObject().getPropertyURI();
		return selectedRelation;
	}


	/**
	 * @param selectedRelation
	 *            the selectedProperty to set
	 */
	public void setSelectedRelation(URI selectedRelation)
	{
		this.selectedRelation = selectedRelation;
		if (selectedRelation != null)
			getModelObject().setPropertyURI(selectedRelation);
	}


	/**
	 * @return the selectedProperty
	 */
	public URI getCustomRelation()
	{
		if (getModelObject() != null)
			return getModelObject().getPropertyURI();
		return null;
	}


	/**
	 * @param selectedRelation
	 *            the selectedProperty to set
	 */
	public void setCustomRelation(URI customRelation)
	{
		if (selectedRelation == null && customRelation != null)
			getModelObject().setPropertyURI(customRelation);
	}


	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}


	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}


	public Statement getModelObject()
	{
		return form.getModelObject();
	}


	public void setModelObject(Statement stmt)
	{
		form.setModelObject(stmt);
	}


	public String getSubjectURIShort()
	{
		if (getModelObject() == null) {
			return null;
		}
		return "./" + roPage.roURI.relativize(getModelObject().getSubjectURI()).toString();
	}
}