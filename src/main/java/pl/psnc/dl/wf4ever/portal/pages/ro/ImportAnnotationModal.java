package pl.psnc.dl.wf4ever.portal.pages.ro;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.model.AggregatedResource;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;

@SuppressWarnings("serial")
class ImportAnnotationModal
	extends Panel
{

	private static final Logger log = Logger.getLogger(ImportAnnotationModal.class);

	private MyFeedbackPanel feedbackPanel;


	public ImportAnnotationModal(String id, final RoPage roPage, final IModel<AggregatedResource> itemModel)
	{
		super(id);

		Form< ? > form = new Form<Void>("uploadResourceForm");
		add(form);

		feedbackPanel = new MyFeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);

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
						roPage.onAnnotationImport(target, uploadedFile, itemModel.getObject());
						target.appendJavaScript("$('#import-annotation-modal').modal('hide')");
					}
					catch (Exception e) {
						log.error("Error when importing annotation", e);
						error(e);
					}
				}
				target.add(feedbackPanel);
			}

		});
		form.add(new MyAjaxButton("cancelUploadResource", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.add(feedbackPanel);
				target.appendJavaScript("$('#import-annotation-modal').modal('hide')");
			}
		}.setDefaultFormProcessing(false));
	}
}
