package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;

@SuppressWarnings("serial")
class UploadResourceModal
	extends Panel
{

	public UploadResourceModal(String id, final RoPage roPage)
	{
		super(id);
		Form< ? > form = new Form<Void>("uploadResourceForm");
		add(form);

		// Enable multipart mode (need for uploads file)
		form.setMultiPart(true);

		// max upload size, 10k
		form.setMaxSize(Bytes.megabytes(10));
		final FileUploadField fileUpload = new FileUploadField("fileUpload");
		form.add(fileUpload);
		form.add(new MyAjaxButton("confirmUploadResource", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				final FileUpload uploadedFile = fileUpload.getFileUpload();
				if (uploadedFile != null) {
					try {
						roPage.onFileUploaded(target, uploadedFile);
						target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
					}
					catch (IOException | URISyntaxException e) {
						error(e);
					}
				}
			}

		});
		form.add(new MyAjaxButton("cancelUploadResource", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#upload-resource-modal').modal('hide')");
			}
		}.setDefaultFormProcessing(false));
	}

}