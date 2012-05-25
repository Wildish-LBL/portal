package pl.psnc.dl.wf4ever.portal.pages.ro;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;
import pl.psnc.dl.wf4ever.portal.pages.util.MyFeedbackPanel;
import pl.psnc.dl.wf4ever.portal.utils.RDFFormat;

@SuppressWarnings("serial")
class DownloadMetadataModal
	extends Panel
{

	private RDFFormat format = RDFFormat.RDFXML;

	private MyFeedbackPanel feedbackPanel;


	public DownloadMetadataModal(String id, final RoPage roPage)
	{
		super(id);
		Form< ? > form = new Form<Void>("downloadMetadataForm");
		add(form);

		feedbackPanel = new MyFeedbackPanel("feedbackPanel");
		feedbackPanel.setOutputMarkupId(true);
		form.add(feedbackPanel);

		List<RDFFormat> formats = Arrays.asList(RDFFormat.RDFXML, RDFFormat.TURTLE, RDFFormat.TRIG, RDFFormat.TRIX,
			RDFFormat.N3);
		DropDownChoice<RDFFormat> formatDropDown = new DropDownChoice<RDFFormat>("rdfFormat",
				new PropertyModel<RDFFormat>(this, "format"), formats, new IChoiceRenderer<RDFFormat>() {

					@Override
					public Object getDisplayValue(RDFFormat format)
					{
						return format.getName();
					}


					@Override
					public String getIdValue(RDFFormat format, int index)
					{
						return "" + index;
					}
				});
		form.add(formatDropDown);
		form.add(new MyAjaxButton("confirmDownloadMetadata", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				roPage.onMetadataDownload(getFormat());
			}
		});
		form.add(new MyAjaxButton("cancelDownloadMetadata", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				target.appendJavaScript("$('#download-metadata-modal').modal('hide')");
			}
		}.setDefaultFormProcessing(false));
	}


	public RDFFormat getFormat()
	{
		return format;
	}


	public void setFormat(RDFFormat format)
	{
		this.format = format;
	}

}