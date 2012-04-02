package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Bytes;

import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;

import com.google.common.collect.Multimap;

@SuppressWarnings("serial")
class UploadResourceModal
	extends Panel
{

	public UploadResourceModal(String id, final RoPage roPage, final Multimap<String, URI> resourceGroups)
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

		List<String> types = new ArrayList<>(resourceGroups.keySet());

		final List<String> selectedTypes = new ArrayList<>();
		CheckGroup<String> group = new CheckGroup<String>("group", selectedTypes);
		form.add(group);
		ListView<String> list = new ListView<String>("resourceTypes", types) {

			@Override
			protected void populateItem(ListItem<String> item)
			{
				Check<String> check = new Check<String>("checkbox", item.getModel());
				item.add(check);
				item.add(new AttributeModifier("for", new Model<String>(check.getMarkupId())));
				item.setEscapeModelStrings(false);
				item.add(new Label("title", item.getModelObject()));
			}
		};
		list.setReuseItems(true);
		group.add(list);

		form.add(new MyAjaxButton("confirmUploadResource", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
			{
				super.onSubmit(target, form);
				final FileUpload uploadedFile = fileUpload.getFileUpload();
				if (uploadedFile != null) {
					try {
						roPage.onFileUploaded(target, uploadedFile, selectedTypes);
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