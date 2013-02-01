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
import org.purl.wf4ever.rosrs.client.Statement;
import org.purl.wf4ever.rosrs.client.Thing;

import pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components.RoTree;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.pages.util.RequiredURITextField;

import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * A modal window for adding/editing relations, i.e. statements about the selected resource and another internal RO
 * resource.
 * 
 * @author piotrekhol
 * 
 */
class RelationEditModal extends Panel {

    /** id. */
    private static final long serialVersionUID = -805443481947725257L;

    /** A list of properties to choose from. */
    public static final URI[] DEFAULT_RELATIONS = { URI.create(DCTerms.source.getURI()),
            URI.create(DCTerms.references.getURI()), URI.create(DCTerms.isReferencedBy.getURI()),
            URI.create(DCTerms.hasVersion.getURI()), URI.create(DCTerms.isVersionOf.getURI()),
            URI.create(DCTerms.isReplacedBy.getURI()), URI.create(DCTerms.replaces.getURI()),
            URI.create(DCTerms.isFormatOf.getURI()), URI.create(DCTerms.hasFormat.getURI()),
            URI.create(DCTerms.isRequiredBy.getURI()), URI.create(DCTerms.requires.getURI()),
            URI.create(DCTerms.hasPart.getURI()), URI.create(DCTerms.isPartOf.getURI()),
            URI.create("http://purl.org/wf4ever/wfprov#describedByWorkflow"),
            URI.create("http://purl.org/wf4ever/wfprov#wasOutputFrom"),
            URI.create("http://purl.org/wf4ever/wfprov#usedInput") };

    /** Selected property. */
    private URI selectedRelation;

    /** Window title. */
    private String title;

    /** Form aggregating input fields. */
    private Form<Statement> form;

    /** Tree with RO resources. */
    private RoTree tree;

    /** Div to show while the resources tree is loading. */
    private Fragment treeLoading;

    /** The owning page. */
    private RoPage roPage;

    /** Window feedback panel. */
    private MyFeedbackPanel feedbackPanel;

    /** List of relations, in case more than one is added. */
    private List<Statement> statements = new ArrayList<>();

    /** Button for adding more relations. */
    private MyAjaxButton anotherButton;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param roPage
     *            owning page
     * @param model
     *            model of currently added/edited relation
     * @param tempRoTreeId
     *            wicket id of a fragment to show while the resources tree is loading
     */
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

        TextField<String> subjectURI = new TextField<String>("subjectURI", new PropertyModel<String>(this,
                "subjectURIShort"));
        subjectURI.setEnabled(false);
        form.add(subjectURI);

        List<URI> choices = Arrays.asList(DEFAULT_RELATIONS);
        DropDownChoice<URI> relations = new DropDownChoice<URI>("relationURI", new PropertyModel<URI>(this,
                "selectedRelation"), choices);
        relations.setNullValid(true);
        form.add(relations);

        final WebMarkupContainer relationURIDiv = new WebMarkupContainer("customRelationURIDiv");
        form.add(relationURIDiv);

        TextField<URI> relationURI = new RequiredURITextField("customRelationURI", new PropertyModel<URI>(this,
                "customRelation"));
        relationURIDiv.add(relationURI);

        tree = new RoTree("objectTree", new PropertyModel<TreeModel>(roPage, "physicalResourcesTree"));
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
                if (!(node.getUserObject() instanceof Thing)) {
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
                Thing res = (Thing) ((DefaultMutableTreeNode) tree.getTreeState().getSelectedNodes().iterator().next())
                        .getUserObject();
                statement.setObjectURI(res.getUri());

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
                Thing res = (Thing) ((DefaultMutableTreeNode) tree.getTreeState().getSelectedNodes().iterator().next())
                        .getUserObject();
                statement.setObjectURI(res.getUri());
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


    /**
     * Call when the RO tree is ready to be displayed.
     */
    public void onRoTreeLoaded() {
        treeLoading.replaceWith(tree);
    }


    /**
     * Relation selected from the list or the property of the relation that is edited.
     * 
     * @return property URI
     */
    public URI getSelectedRelation() {
        if (selectedRelation == null && getModelObject() != null) {
            return getModelObject().getPropertyURI();
        }
        return selectedRelation;
    }


    /**
     * Set relation from the list and, if the statement is edited, the statement property.
     * 
     * @param selectedRelation
     *            relation URI
     */
    public void setSelectedRelation(URI selectedRelation) {
        this.selectedRelation = selectedRelation;
        if (selectedRelation != null) {
            getModelObject().setPropertyURI(selectedRelation);
        }
    }


    /**
     * Get user custom relation property URI.
     * 
     * @return property URI or null
     */
    public URI getCustomRelation() {
        if (getModelObject() != null) {
            return getModelObject().getPropertyURI();
        }
        return null;
    }


    /**
     * Set custom user relation property, provided that there is no relation property selected from the list.
     * 
     * @param customRelation
     *            custom relation property URI
     */
    public void setCustomRelation(URI customRelation) {
        if (selectedRelation == null && customRelation != null) {
            getModelObject().setPropertyURI(customRelation);
        }
    }


    public String getTitle() {
        return title;
    }


    private void setTitle(String title) {
        this.title = title;
    }


    /**
     * Get the statement that is added/edited.
     * 
     * @return a statement
     */
    public Statement getModelObject() {
        return form.getModelObject();
    }


    /**
     * Set the statement that is added/edited.
     * 
     * @param stmt
     *            statement
     */
    public void setModelObject(Statement stmt) {
        form.setModelObject(stmt);
    }


    /**
     * A short version of the selected resource URI.
     * 
     * @return resource URI relative to RO URI
     */
    public String getSubjectURIShort() {
        if (getModelObject() == null) {
            return null;
        }
        return "./" + roPage.researchObject.getUri().relativize(getModelObject().getSubjectURI()).toString();
    }


    // FIXME not the best design probably, these modals might use some refactoring
    /**
     * Set window title to "Add relation", enable adding multiple relations.
     */
    public void setAddMode() {
        setTitle("Add relation");
        getAnotherButton().setVisible(true);
    }


    /**
     * Set window title to "Edit relation", disable adding multiple relations.
     */
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
