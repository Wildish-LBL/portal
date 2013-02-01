package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.forms;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmitListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import pl.psnc.dl.wf4ever.portal.ui.forms.fields.UriTextField;

/**
 * Hidden form supporting drag and drop files displacement.
 * 
 * @author pejot
 * 
 */
public class FilesShiftForm extends Panel {

    /** Serialziation. */
    private static final long serialVersionUID = 1L;
    /** Form. */
    private Form<FilesShiftModel> form;
    /** Internal model. */
    private FilesShiftModel filesShiftModel;
    /** Logger. */
    private static final Logger LOG = Logger.getLogger(FilesShiftForm.class);
    /** Listeners. */
    private ArrayList<IFormSubmitListener> formSubmitListeners;
    /** Component to refresh once the form is submitted. */
    private Component refreshableComponent;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param refreshableComponent
     *            component to refresh once the form is submitted
     */
    public FilesShiftForm(String id, final Component refreshableComponent) {
        super(id);
        //setting variables
        this.refreshableComponent = refreshableComponent;
        filesShiftModel = new FilesShiftModel();
        formSubmitListeners = new ArrayList<IFormSubmitListener>();
        //building UI
        UriTextField folderUriTextField = new UriTextField("folderUri");
        UriTextField fileUriTextField = new UriTextField("fileUri");

        form = new Form<FilesShiftModel>("form", new CompoundPropertyModel<FilesShiftModel>(filesShiftModel));
        form.add(new AjaxButton("ajax-submit") {

            /** Serialziation */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                for (IFormSubmitListener listener : formSubmitListeners) {
                    listener.onFormSubmitted();
                }
                target.add(refreshableComponent);
            }


            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                // TODO Auto-generated method stub

            }
        });
        form.add(folderUriTextField);
        form.add(fileUriTextField);
        add(form);
    }


    public URI getFolderUri() {
        return filesShiftModel.getFolderUri();
    }


    public URI getFIleUri() {
        return filesShiftModel.getFileUri();
    }


    /**
     * Add new submit event listener.
     * 
     * @param formSubmitListener
     *            listener
     */
    public void addOnSubmitListener(IFormSubmitListener formSubmitListener) {
        formSubmitListeners.add(formSubmitListener);
    }


    /**
     * Internal model class.
     * 
     * @author pejot
     * 
     */
    class FilesShiftModel implements Serializable {

        /** Serialization. */
        private static final long serialVersionUID = 1L;
        /** Folder uri. */
        private URI folderUri;
        /** File uri. */
        private URI fileUri;


        public URI getFileUri() {
            return fileUri;
        }


        public void setFileUri(URI fileUri) {
            this.fileUri = (fileUri);
        }


        public URI getFolderUri() {
            return folderUri;
        }


        public void setFolderUri(URI folderUri) {
            this.folderUri = (folderUri);
        }

    }
}
