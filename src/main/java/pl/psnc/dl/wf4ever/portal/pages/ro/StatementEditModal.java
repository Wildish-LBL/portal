package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;
import java.util.ArrayList;
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
import org.purl.wf4ever.rosrs.client.Statement;

import pl.psnc.dl.wf4ever.portal.components.UniversalStyledAjaxButton;
import pl.psnc.dl.wf4ever.portal.components.feedback.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.components.form.URIConverter;

import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Annotation statement edition modal.
 * 
 * @author piotrekhol
 * 
 */
class StatementEditModal extends Panel {

    /** id. */
    private static final long serialVersionUID = -805443481947725257L;

    /** Default properties to suggest to the user. */
    public static final URI[] DEFAULT_PROPERTIES = { URI.create(DCTerms.type.getURI()),
            URI.create(DCTerms.subject.getURI()), URI.create(DCTerms.description.getURI()),
            URI.create(DCTerms.format.getURI()), URI.create(DCTerms.title.getURI()),
            URI.create(DCTerms.created.getURI()) };

    /** Literal statement object. */
    private final TextArea<String> value;

    /** Resource statement object. */
    private final TextField<URI> objectURI;

    /** Statement property URI. */
    private final TextField<URI> propertyURI;

    /** A property selected from DEFAULT_PROPERTIES. */
    private URI selectedProperty;

    /** Window title. */
    private String title;

    /** The form. */
    private Form<Statement> form;

    /** Feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** List of statements for a bulk creation. */
    private List<Statement> statements = new ArrayList<>();

    /** A button for a bulk creation. */
    private UniversalStyledAjaxButton anotherButton;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param roPage
     *            owning page
     * @param model
     *            statement model, with a non-null object for editing
     */
    @SuppressWarnings("serial")
    public StatementEditModal(String id, final RoPage roPage, CompoundPropertyModel<Statement> model) {
        super(id, model);
        setOutputMarkupId(true);
        form = new Form<>("stmtEditForm", model);
        add(form);

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new Label("title", new PropertyModel<String>(this, "title")));

        List<URI> choices = Arrays.asList(DEFAULT_PROPERTIES);
        DropDownChoice<URI> properties = new DropDownChoice<URI>("propertyURI", new PropertyModel<URI>(this,
                "selectedProperty"), choices);
        properties.setNullValid(true);
        form.add(properties);

        final WebMarkupContainer propertyURIDiv = new WebMarkupContainer("customPropertyURIDiv");
        form.add(propertyURIDiv);

        propertyURI = new TextField<URI>("customPropertyURI", new PropertyModel<URI>(this, "customProperty"), URI.class) {

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new URIConverter();
            }
        };
        propertyURIDiv.add(propertyURI);

        final WebMarkupContainer uriDiv = new WebMarkupContainer("objectURIDiv");
        form.add(uriDiv);

        objectURI = new TextField<URI>("objectURI", URI.class) {

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
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

        form.add(new UniversalStyledAjaxButton("save", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Statement statement = StatementEditModal.this.getModelObject();
                try {
                    if (statement.getAnnotation() == null) {
                        statements.add(statement);
                        roPage.onStatementAdd(statements);
                    } else {
                        roPage.onStatementEdit(statement);
                    }
                    statements.clear();
                    roPage.onStatementAddedEdited(target);
                    target.add(form);
                    target.appendJavaScript("$('#edit-ann-modal').modal('hide')");
                } catch (Exception e) {
                    error(e.getMessage());
                }
                target.add(feedbackPanel);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(feedbackPanel);
            }
        });
        anotherButton = new UniversalStyledAjaxButton("another", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Statement statement = StatementEditModal.this.getModelObject();
                statements.add(statement);
                try {
                    StatementEditModal.this.setModelObject(new Statement(statement.getSubjectURI(), null));
                    target.add(form);
                    target.appendJavaScript("showStmtEdit('');");
                } catch (Exception e) {
                    error(e.getMessage());
                }
                target.add(feedbackPanel);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(feedbackPanel);
            }
        };
        form.add(anotherButton);
        form.add(new UniversalStyledAjaxButton("cancel", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                statements.clear();
                target.appendJavaScript("$('#edit-ann-modal').modal('hide')");
            }
        }.setDefaultFormProcessing(false));
    }


    /**
     * Get the statement property, a default or custom one.
     * 
     * @return property URI
     */
    public URI getSelectedProperty() {
        if (selectedProperty == null && getModelObject() != null) {
            return getModelObject().getPropertyURI();
        }
        return selectedProperty;
    }


    /**
     * Set the statement property.
     * 
     * @param selectedProperty
     *            property URI
     */
    public void setSelectedProperty(URI selectedProperty) {
        this.selectedProperty = selectedProperty;
        if (selectedProperty != null) {
            getModelObject().setPropertyURI(selectedProperty);
        }
    }


    /**
     * Get the custom property.
     * 
     * @return property URI or null
     */
    public URI getCustomProperty() {
        if (getModelObject() != null) {
            return getModelObject().getPropertyURI();
        }
        return null;
    }


    /**
     * Set the custom property.
     * 
     * @param customProperty
     *            property URI or null
     */
    public void setCustomProperty(URI customProperty) {
        if (selectedProperty == null && customProperty != null) {
            getModelObject().setPropertyURI(customProperty);
        }
    }


    public String getTitle() {
        return title;
    }


    private void setTitle(String title) {
        this.title = title;
    }


    public Statement getModelObject() {
        return form.getModelObject();
    }


    /**
     * Utility method for setting the model object.
     * 
     * @param stmt
     *            a statement
     */
    public void setModelObject(Statement stmt) {
        form.setModelObject(stmt);
    }


    /**
     * Prepare the window for adding statements.
     */
    // FIXME not the best design probably, these modals might use some refactoring
    public void setAddMode() {
        setTitle("Add annotation");
        getAnotherButton().setVisible(true);
    }


    /**
     * Prepare the window for editing statements.
     */
    public void setEditMode() {
        setTitle("Edit annotation");
        getAnotherButton().setVisible(false);
    }


    public UniversalStyledAjaxButton getAnotherButton() {
        return anotherButton;
    }


    public void setAnotherButton(UniversalStyledAjaxButton anotherButton) {
        this.anotherButton = anotherButton;
    }

}
