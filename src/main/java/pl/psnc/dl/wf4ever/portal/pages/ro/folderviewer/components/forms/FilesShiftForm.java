package pl.psnc.dl.wf4ever.portal.pages.ro.folderviewer.components.forms;

import java.io.Serializable;
import java.net.URI;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
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


    /**
     * Constructor.
     * 
     * @param id
     */
    public FilesShiftForm(String id) {
        super(id);
        filesShiftModel = new FilesShiftModel();
        //building UI
        UriTextField folderUriTextField = new UriTextField("folderUri");
        UriTextField fileUriTextField = new UriTextField("fileUri");
        form = new Form<FilesShiftModel>("form", new CompoundPropertyModel<FilesShiftModel>(filesShiftModel)) {

            /** Serialization */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit() {
                super.onSubmit();
                form.clearInput();
            }
        };
        form.add(folderUriTextField);
        form.add(fileUriTextField);
        add(form);
    }


    /**
     * Internal model class
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
