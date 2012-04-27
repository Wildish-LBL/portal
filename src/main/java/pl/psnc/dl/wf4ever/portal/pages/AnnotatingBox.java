package pl.psnc.dl.wf4ever.portal.pages;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.scribe.model.Token;

import pl.psnc.dl.wf4ever.portal.MySession;
import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.model.Annotation;
import pl.psnc.dl.wf4ever.portal.model.Creator;
import pl.psnc.dl.wf4ever.portal.model.RoFactory;
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.pages.util.CreatorsPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.services.ROSRService;

@SuppressWarnings("serial")
class AnnotatingBox
	extends WebMarkupContainer
{

	/**
	 * 
	 */
	private final RoPage roPage;

	final WebMarkupContainer annotationsDiv;

	final PropertyListView<Annotation> annList;

	final List<Statement> selectedStatements = new ArrayList<Statement>();


	public AggregatedResource getModelObject()
	{
		return (AggregatedResource) getDefaultModelObject();
	}


	public AnnotatingBox(RoPage roPage, final CompoundPropertyModel<AggregatedResource> itemModel)

	{
		super("annotatingBox", itemModel);
		this.roPage = roPage;
		setOutputMarkupId(true);
		add(new Label("annTarget", itemModel.<URI> bind("URI")));

		annotationsDiv = new WebMarkupContainer("annotationsDiv");
		annotationsDiv.setOutputMarkupId(true);
		add(annotationsDiv);

		Form< ? > annForm = new Form<Void>("annotationsForm");
		annotationsDiv.add(annForm);
		CheckGroup<Statement> group = new CheckGroup<Statement>("group", selectedStatements);
		annForm.add(group);

		annList = new PropertyListView<Annotation>("annotationsList", new PropertyModel<List<Annotation>>(itemModel,
				"annotations")) {

			@Override
			protected void populateItem(ListItem<Annotation> item)
			{
				final Annotation annotation = item.getModelObject();
				item.add(new AttributeAppender("title", new PropertyModel<URI>(annotation, "URI")));
				PropertyListView<Statement> statementsList = new PropertyListView<Statement>("statementsList",
						new PropertyModel<List<Statement>>(annotation, "body")) {

					@Override
					protected void populateItem(final ListItem<Statement> item)
					{
						final Statement statement = item.getModelObject();
						item.add(new Check<Statement>("checkbox", item.getModel()));
						if (statement.isSubjectURIResource()) {
							if (RoFactory
									.isResourceInternal(AnnotatingBox.this.roPage.roURI, statement.getSubjectURI())) {
								if (statement.getSubjectURI().equals(itemModel.getObject().getURI())) {
									item.add(new Label("subject", "[This item]"));
								}
								else {
									item.add(AnnotatingBox.this.roPage.new InternalLinkFragment("subject",
											"internalLinkFragment", AnnotatingBox.this.roPage, statement));
								}
							}
							else {
								item.add(AnnotatingBox.this.roPage.new ExternalLinkFragment("subject",
										"externalLinkFragment", AnnotatingBox.this.roPage,
										(CompoundPropertyModel<Statement>) item.getModel(), "subjectURI"));
							}
						}
						else {
							item.add(new Label("subject", ((CompoundPropertyModel<Statement>) item.getModel())
									.<String> bind("subjectValue")).setEscapeModelStrings(false));
						}
						item.add(new Label("propertyLocalNameNice"));
						if (statement.isObjectURIResource()) {
							item.add(AnnotatingBox.this.roPage.new ExternalLinkFragment("object",
									"externalLinkFragment", AnnotatingBox.this.roPage,
									(CompoundPropertyModel<Statement>) item.getModel(), "objectURI"));
						}
						else {
							item.add(new Label("object", ((CompoundPropertyModel<Statement>) item.getModel())
									.<String> bind("objectValue")).setEscapeModelStrings(false));
						}
						if (AnnotatingBox.this.roPage.canEdit) {
							item.add(AnnotatingBox.this.roPage.new EditLinkFragment("edit", "editLinkFragment",
									AnnotatingBox.this.roPage, new AjaxFallbackLink<String>("link") {

										@Override
										public void onClick(AjaxRequestTarget target)
										{
											AnnotatingBox.this.roPage.stmtEditForm.setModelObject(statement);
											AnnotatingBox.this.roPage.stmtEditForm.setTitle("Edit statement");
											target.add(AnnotatingBox.this.roPage.stmtEditForm);
											target.appendJavaScript("showStmtEdit('"
													+ StringEscapeUtils.escapeEcmaScript(statement.getObjectValue())
													+ "');");
										}
									}));
						}
						else {
							item.add(new Label("edit", "Edit"));
						}
					}
				};
				item.add(statementsList);
				item.add(new CreatorsPanel("creator", new PropertyModel<List<Creator>>(itemModel, "creators")));
				item.add(new Label("createdFormatted"));
			}
		};
		group.add(annList);

		AjaxButton addStatement = new MyAjaxButton("addAnnotation", annForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				try {
					AnnotatingBox.this.roPage.stmtEditForm.setModelObject(new Statement(itemModel.getObject().getURI(),
							null));
					AnnotatingBox.this.roPage.stmtEditForm.setTitle("Add annotation");
					target.add(AnnotatingBox.this.roPage.stmtEditForm);
					target.appendJavaScript("showStmtEdit('');");
				}
				catch (Exception e) {
					error(e.getMessage());
				}
			}


			@Override
			public boolean isEnabled()
			{
				return super.isEnabled() && AnnotatingBox.this.roPage.canEdit;
			}
		};
		annForm.add(addStatement);

		AjaxButton deleteStatement = new MyAjaxButton("deleteAnnotation", annForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				Token dLibraToken = getSession().getdLibraAccessToken();
				List<Annotation> annotations = new ArrayList<Annotation>();
				for (Statement statement : selectedStatements) {
					statement.getAnnotation().getBody().remove(statement);
					annotations.add(statement.getAnnotation());
					AggregatedResource subjectAR = AnnotatingBox.this.roPage.resources.get(statement.getSubjectURI());
					AggregatedResource objectAR = AnnotatingBox.this.roPage.resources.get(statement.getObjectURI());
					if (subjectAR != null && objectAR != null) {
						subjectAR.getRelations().remove(statement.getPropertyLocalNameNice(), objectAR);
					}
				}
				for (Annotation annotation : annotations) {
					try {
						if (annotation.getBody().isEmpty()) {
							ROSRService.deleteResource(annotation.getBodyURI(), dLibraToken);
							ROSRService.deleteAnnotation(AnnotatingBox.this.roPage.roURI, annotation.getURI(),
								dLibraToken);
						}
						else {
							ROSRService.sendResource(annotation.getBodyURI(),
								RoFactory.wrapAnnotationBody(annotation.getBody()), "application/rdf+xml", dLibraToken);
						}
					}
					catch (Exception e) {
						error(e);
					}
				}
				//					roFactory.reload();
				AnnotatingBox.this.getModelObject().setAnnotations(
					RoFactory.createAnnotations(AnnotatingBox.this.roPage.roURI, AnnotatingBox.this.getModelObject()
							.getURI(), MySession.get().getUsernames()));
				selectedStatements.clear();
				target.add(AnnotatingBox.this.roPage.annotatingBox.annotationsDiv);
				target.add(AnnotatingBox.this.roPage.roViewerBox.infoPanel);
			}


			@Override
			public boolean isEnabled()
			{
				return super.isEnabled() && AnnotatingBox.this.roPage.canEdit;
			}
		};
		annForm.add(deleteStatement);

		AjaxButton addRelation = new MyAjaxButton("addRelation", annForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				try {
					AnnotatingBox.this.roPage.relEditForm.setModelObject(new Statement(itemModel.getObject().getURI(),
							null));
					AnnotatingBox.this.roPage.relEditForm.setTitle("Add relation");
					target.add(AnnotatingBox.this.roPage.relEditForm);
					target.appendJavaScript("showRelEdit();");
				}
				catch (Exception e) {
					error(e.getMessage());
				}
			}


			@Override
			public boolean isEnabled()
			{
				return super.isEnabled() && AnnotatingBox.this.roPage.canEdit;
			}
		};
		annForm.add(addRelation);
	}
}
