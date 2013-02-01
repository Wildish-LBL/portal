package pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components;

import java.net.URI;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.pages.ro.roexplorer.components.modals.DownloadMetadataModal;
import pl.psnc.dl.wf4ever.portal.ui.components.UniversalStyledAjaxButton;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

/**
 * Buttons bar for selected folder or resource.
 * 
 * @author pejot
 * 
 */
public class ROButtonsBar extends Panel {

    /** RO URI. */
    URI roURI;

    /** Serialziation. */
    private static final long serialVersionUID = 1L;

    /** Download RO metadata button. */
    private UniversalStyledAjaxButton downloadROMetadata;

    /** Download RO ZIP button. */
    private ExternalLink downloadROZipped;

    /** Fake form for ajax buttons. */
    Form<?> roForm;


    /**
     * Constructor.
     * 
     * @param id
     *            wicket id
     */
    public ROButtonsBar(String id, URI roURI) {
        super(id);
        this.roURI = roURI;
        setOutputMarkupId(true);
        roForm = new Form<Void>("roForm");
        add(new DownloadMetadataModal("downloadMetadataModal", this));
        downloadROZipped = new ExternalLink("downloadROZipped", new PropertyModel<String>(this, "ROZipLink"));
        downloadROMetadata = new UniversalStyledAjaxButton("downloadMetadata", roForm) {

            /** Serialization. */
            private static final long serialVersionUID = 1L;


            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                target.appendJavaScript("$('#download-metadata-modal').modal('show')");
            }

        };
        roForm.add(downloadROZipped);
        roForm.add(downloadROMetadata);
        add(roForm);
    }


    /**
     * Get link to zipped RO
     * 
     * @return a link to Zipped RO
     */
    public String getROZipLink() {
        return roURI.toString().replaceFirst("/ROs/", "/zippedROs/");
    }


    /**
     * Return a link to a format-specific version of the manifest.
     * 
     * @param format
     *            RDF format
     * @return a URI as string of the resource
     */
    public String getROMetadataLink(RDFFormat format) {
        return roURI.resolve(".ro/manifest." + format.getDefaultFileExtension() + "?original=manifest.rdf").toString();
    }
}
