package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.purl.wf4ever.rosrs.client.Resource;

import pl.psnc.dl.wf4ever.portal.listeners.IAjaxLinkListener;

/**
 * List of files displayed as tails.
 * 
 * @author pejot
 * 
 */
public class FilesPanel extends Panel {

    /** List of files. */
    private ListView<Resource> listView;
    /** Serialization. */
    private static final long serialVersionUID = 1L;
    /** CSS tail resource reference. */
    private CssResourceReference cssResourceReference = new CssResourceReference(FilesPanel.class, "tiles.css");
    /** Listeners for the selected resource. */
    private List<IAjaxLinkListener> listeners;
    /** Folders model. */
    private IModel<List<Resource>> foldersModel;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     * @param foldersModel
     *            folders model
     */
    public FilesPanel(String id, IModel<List<Resource>> foldersModel) {
        super(id, new Model<Resource>());
        setOutputMarkupId(true);

        this.foldersModel = foldersModel;
        listeners = new ArrayList<IAjaxLinkListener>();
        listView = new PropertyListView<Resource>("filesListView", foldersModel) {

            private static final long serialVersionUID = -6310254217773728128L;


            @SuppressWarnings("rawtypes")
            @Override
            protected void populateItem(final ListItem<Resource> item) {

                final Resource thing = item.getModelObject();

                final Label label = new Label("tailLabel", calculateLabel(item.getModelObject().getName()));

                @SuppressWarnings("unchecked")
                final AjaxLink<Object> link = new AjaxLink<Object>("tailLink", new Model()) {

                    /** Serialization */
                    private static final long serialVersionUID = 6984606437114241889L;


                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        if (thing.equals(getSelectedFile())) {
                            unselect();
                        } else {
                            setSelectedFile(thing);
                        }
                        for (IAjaxLinkListener listener : listeners) {
                            listener.onAjaxLinkClicked(thing, target);
                        }
                    }
                };

                link.add(AttributeModifier.replace("uri", item.getModelObject().getUri()));
                item.add(link);
                assignIcon(item);
                link.add(label);
            }

        };

        add(listView);

    }


    /**
     * Calculate tile label basing on resource name.
     * 
     * @param name
     *            object name
     * @return label
     */
    private String calculateLabel(String name) {
        if (name.length() <= 35) {
            return name;
        } else {
            return name.substring(0, 31) + "...";
        }
    }


    /**
     * Add new link listener.
     * 
     * @param listener
     *            link listener
     */
    public void addLinkListeners(IAjaxLinkListener listener) {
        listeners.add(listener);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(cssResourceReference);
        super.renderHead(response);
    }


    @SuppressWarnings("unchecked")
    public IModel<Resource> getModel() {
        return (IModel<Resource>) this.getDefaultModel();
    }


    /**
     * set selected file.
     * 
     * @param resource
     *            resource
     */
    public void setSelectedFile(Resource resource) {
        getModel().setObject(resource);
    }


    public Resource getSelectedFile() {
        return getModel().getObject();
    }


    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (foldersModel.getObject() != null && foldersModel.getObject().size() > 1) {
            //show with scroll
        } else {
            //show without scroll
        }
    }


    /**
     * Unselect selected file from outside (For example on node click).
     */
    public void unselect() {
        setSelectedFile(null);
    }


    /**
     * Assign icon with respect to the mimetype.
     * 
     * @param item
     *            resource from the file list.
     */
    private void assignIcon(ListItem<Resource> item) {
        Resource resource = (Resource) item.getDefaultModelObject();
        String name = resource.getName();
        if (name.endsWith(".txt") || name.endsWith(".text")) {
            item.add(new ContextImage("image", "images/mimetypes/txt.png"));
        } else if (name.endsWith(".doc") || name.endsWith(".odt") || name.endsWith(".rtf") || name.endsWith(".doc")) {
            item.add(new ContextImage("image", "images/mimetypes/doc.png"));
        } else if (name.endsWith(".pdf")) {
            item.add(new ContextImage("image", "images/mimetypes/pdf.png"));
        } else if (name.endsWith(".xls") || name.endsWith(".xlsx") || name.endsWith(".ods")) {
            item.add(new ContextImage("image", "images/mimetypes/xls.png"));
        } else if (name.endsWith(".rdf") || name.endsWith(".ttl") || name.endsWith(".owl")) {
            item.add(new ContextImage("image", "images/mimetypes/rdf.png"));
        } else if (name.endsWith(".zip") || name.endsWith(".tar") || name.endsWith(".tar.gz") || name.endsWith(".rar")) {
            item.add(new ContextImage("image", "images/mimetypes/zip.png"));
        } else if (name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".bmp") || name.endsWith(".gif")
                || name.endsWith(".jpg")) {
            item.add(new ContextImage("image", "images/mimetypes/jpeg.png"));
        } else if (name.endsWith(".avi") || name.endsWith(".wmv") || name.endsWith(".3gp")) {
            item.add(new ContextImage("image", "images/mimetypes/avi.png"));
        } else if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".midi")) {
            item.add(new ContextImage("image", "images/mimetypes/mp3.png"));
        } else if (name.endsWith(".wfbundle")) {
            item.add(new ContextImage("image", "images/mimetypes/wfbundle.png"));
        } else {
            item.add(new ContextImage("image", "images/mimetypes/blank.png"));
        }

    }
}
