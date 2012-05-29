package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.net.URI;
import java.util.ArrayList;
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
import pl.psnc.dl.wf4ever.portal.model.Statement;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.RoTree;
import pl.psnc.dl.wf4ever.portal.pages.util.URIConverter;

import com.hp.hpl.jena.vocabulary.DCTerms;

class RelationEditModal extends Panel {

    /**
	 * 
	 */
    private static final long serialVersionUID = -805443481947725257L;
    public static final URI[] defaultRelations = { URI.create(DCTerms.source.getURI()),
            URI.create(DCTerms.references.getURI()), URI.create(DCTerms.isReferencedBy.getURI()),
            URI.create(DCTerms.hasVersion.getURI()), URI.create(DCTerms.isVersionOf.getURI()),
            URI.create(DCTerms.isReplacedBy.getURI()), URI.create(DCTerms.replaces.getURI()),
            URI.create(DCTerms.isFormatOf.getURI()), URI.create(DCTerms.hasFormat.getURI()),
            URI.create(DCTerms.isRequiredBy.getURI()), URI.create(DCTerms.requires.getURI()),
            URI.create(DCTerms.hasPart.getURI()), URI.create(DCTerms.isPartOf.getURI()),
            URI.create("http://purl.org/wf4ever/wfprov#describedByWorkflow"),
            URI.create("http://purl.org/wf4ever/wfprov#wasOutputFrom"),
            URI.create("http://purl.org/wf4ever/wfprov#usedInput") };

    private final TextField<String> subjectURI;

    private final TextField<URI> relationURI;

    private URI selectedRelation;

    private String title;

    private Form<Statement> form;

    private RoTree tree;

    private Fragment treeLoading;

    private RoPage roPage;

    private MyFeedbackPanel feedbackPanel;

    private List<Statement> statements = new ArrayList<>();

    private MyAjaxButton anotherButton;


    @SuppressWarnings("serial")
    public RelationEditModal(String id, final RoPage roPage, CompoundPropertyModel<Statement> model, String tempRoTreeId) {
        super(id, model);
        this.roPage = roPage;
        setOutputMarkupId(true);
        form = new Form<>("relEditForm", model);
        add(form);

        feedbackPanel = new MyFeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new Label("title", new PropertyModel<String>(this, "title")));

        subjectURI = new TextField<String>("subjectURI", new PropertyModel<String>(this, "subjectURIShort"));
        subjectURI.setEnabled(false);
        form.add(subjectURI);

        List<URI> choices = Arrays.asList(defaultRelations);
        DropDownChoice<URI> relations = new DropDownChoice<URI>("relationURI", new PropertyModel<URI>(this,
                "selectedRelation"), choices);
        relations.setNullValid(true);
        form.add(relations);

        final WebMarkupContainer relationURIDiv = new WebMarkupContainer("customRelationURIDiv");
        form.add(relationURIDiv);

        relationURI = new TextField<URI>("customRelationURI", new PropertyModel<URI>(this, "customRelation"), URI.class) {

            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(Class<C> type) {
                return (IConverter<C>) new URIConverter();
            }
        };
        relationURIDiv.add(relationURI);

        tree = new RoTree("objectTree", new PropertyModel<TreeModel>(roPage, "conceptualResourcesTree"));
        treeLoading = new Fragment("objectTree", tempRoTreeId, roPage);
        form.add(treeLoading);

        form.add(new IFormValidator() {

            @Override
            public void validate(Form<?> form) {
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
            public FormComponent<?>[] getDependentFormComponents() {
                return null;
            }
        });

        form.add(new MyAjaxButton("save", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Statement statement = RelationEditModal.this.getModelObject();
                AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) tree.getTreeState()
                        .getSelectedNodes().iterator().next()).getUserObject();
                statement.setObjectURI(res.getURI());

                try {
                    if (statement.getAnnotation() == null) {
                        statements.add(statement);
                        roPage.onStatementAdd(statements);
                    } else {
                        roPage.onStatementEdit(statement);
                    }
                    statements.clear();
                    roPage.onRelationAddedEdited(statement, target);
                    target.add(form);
                    target.appendJavaScript("$('#edit-rel-modal').modal('hide')");
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
        anotherButton = new MyAjaxButton("another", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                Statement statement = RelationEditModal.this.getModelObject();
                AggregatedResource res = (AggregatedResource) ((DefaultMutableTreeNode) tree.getTreeState()
                        .getSelectedNodes().iterator().next()).getUserObject();
                statement.setObjectURI(res.getURI());
                statements.add(statement);
                try {
                    RelationEditModal.this.setModelObject(new Statement(statement.getSubjectURI(), null));
                    target.add(form);
                    target.appendJavaScript("showRelEdit('');");
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
        form.add(new MyAjaxButton("cancel", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                statements.clear();
                target.appendJavaScript("$('#edit-rel-modal').modal('hide')");
            }
        }.setDefaultFormProcessing(false));
    }


    public void onRoTreeLoaded() {
        treeLoading.replaceWith(tree);
    }


    /**
     * @return the selectedProperty
     */
    public URI getSelectedRelation() {
        if (selectedRelation == null && getModelObject() != null)
            return getModelObject().getPropertyURI();
        return selectedRelation;
    }


    /**
     * @param selectedRelation
     *            the selectedProperty to set
     */
    public void setSelectedRelation(URI selectedRelation) {
        this.selectedRelation = selectedRelation;
        if (selectedRelation != null)
            getModelObject().setPropertyURI(selectedRelation);
    }


    /**
     * @return the selectedProperty
     */
    public URI getCustomRelation() {
        if (getModelObject() != null)
            return getModelObject().getPropertyURI();
        return null;
    }


    /**
     * @param selectedRelation
     *            the selectedProperty to set
     */
    public void setCustomRelation(URI customRelation) {
        if (selectedRelation == null && customRelation != null)
            getModelObject().setPropertyURI(customRelation);
    }


    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }


    /**
     * @param title
     *            the title to set
     */
    private void setTitle(String title) {
        this.title = title;
    }


    public Statement getModelObject() {
        return form.getModelObject();
    }


    public void setModelObject(Statement stmt) {
        form.setModelObject(stmt);
    }


    public String getSubjectURIShort() {
        if (getModelObject() == null) {
            return null;
        }
        return "./" + roPage.roURI.relativize(getModelObject().getSubjectURI()).toString();
    }


    // FIXME not the best design probably, these modals might use some refactoring
    public void setAddMode() {
        setTitle("Add relation");
        getAnotherButton().setVisible(true);
    }


    public void setEditMode() {
        setTitle("Edit relation");
        getAnotherButton().setVisible(false);
    }


    public MyAjaxButton getAnotherButton() {
        return anotherButton;
    }


    public void setAnotherButton(MyAjaxButton anotherButton) {
        this.anotherButton = anotherButton;
    }

}