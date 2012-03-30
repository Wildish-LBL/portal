package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;

import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.URIConverter;

class StatementEditModal
	extends Panel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -805443481947725257L;

	private final TextArea<String> value;

	private final TextField<URI> objectURI;

	private final TextField<URI> propertyURI;

	private URI selectedProperty;

	private String title;

	private Form<Statement> form;


	@SuppressWarnings("serial")
	public StatementEditModal(String id, final RoPage roPage, CompoundPropertyModel<Statement> model)
	{
		super(id, model);
		setOutputMarkupId(true);
		form = new Form<>("stmtEditForm", model);
		add(form);

		form.add(new Label("title", new PropertyModel<String>(this, "title")));

		List<URI> choices = Arrays.asList(RoFactory.defaultProperties);
		DropDownChoice<URI> properties = new DropDownChoice<URI>("propertyURI", new PropertyModel<URI>(this,
				"selectedProperty"), choices);
		properties.setNullValid(true);
		form.add(properties);

		final WebMarkupContainer propertyURIDiv = new WebMarkupContainer("customPropertyURIDiv");
		form.add(propertyURIDiv);

		propertyURI = new TextField<URI>("customPropertyURI", new PropertyModel<URI>(this, "customProperty"), URI.class) {

			@SuppressWarnings("unchecked")
			@Override
			public <C> IConverter<C> getConverter(Class<C> type)
			{
				return (IConverter<C>) new URIConverter();
			}
		};
		propertyURIDiv.add(propertyURI);

		final WebMarkupContainer uriDiv = new WebMarkupContainer("objectURIDiv");
		form.add(uriDiv);

		objectURI = new TextField<URI>("objectURI", URI.class) {

			@SuppressWarnings("unchecked")
			@Override
			public <C> IConverter<C> getConverter(Class<C> type)
			{
				return (IConverter<C>) new URIConverter();
			}
		};
		uriDiv.add(objectURI);

		final WebMarkupContainer valueDiv = new WebMarkupContainer("objectValueDiv");
		form.add(valueDiv);

		value = new TextArea<String>("objectValue");
		value.setEscapeModelStrings(false);
		valueDiv.add(value);

		CheckBox objectType = new CheckBox("objectURIResource");
		form.add(objectType);

		form.add(new MyAjaxButton("save", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				Statement statement = StatementEditModal.this.getModelObject();
				try {
					if (statement.getAnnotation() == null) {
						roPage.onStatementAdd(statement);
					}
					else {
						roPage.onStatementEdit(statement);
					}
					roPage.onStatementAddedEdited(target);
					target.add(form);
					target.appendJavaScript("$('#edit-ann-modal').modal('hide')");
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
				target.appendJavaScript("$('#edit-ann-modal').modal('hide')");
			}
		}.setDefaultFormProcessing(false));
	}


	/**
	 * @return the selectedProperty
	 */
	public URI getSelectedProperty()
	{
		if (selectedProperty == null && getModelObject() != null)
			return getModelObject().getPropertyURI();
		return selectedProperty;
	}


	/**
	 * @param selectedProperty
	 *            the selectedProperty to set
	 */
	public void setSelectedProperty(URI selectedProperty)
	{
		this.selectedProperty = selectedProperty;
		if (selectedProperty != null)
			getModelObject().setPropertyURI(selectedProperty);
	}


	/**
	 * @return the selectedProperty
	 */
	public URI getCustomProperty()
	{
		if (getModelObject() != null)
			return getModelObject().getPropertyURI();
		return null;
	}


	/**
	 * @param selectedProperty
	 *            the selectedProperty to set
	 */
	public void setCustomProperty(URI customProperty)
	{
		if (selectedProperty == null && customProperty != null)
			getModelObject().setPropertyURI(customProperty);
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

}