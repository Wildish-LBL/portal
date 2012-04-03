package pl.psnc.dl.wf4ever.portal.pages;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import pl.psnc.dl.wf4ever.portal.model.ResourceGroup;
import pl.psnc.dl.wf4ever.portal.pages.util.MyAjaxButton;

@SuppressWarnings("serial")
class UploadResourceModal
	extends Panel
{

	public UploadResourceModal(String id, final RoPage roPage, final Set<ResourceGroup> set)
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

		List<ResourceGroup> types = new ArrayList<>(set);

		final Set<ResourceGroup> selectedTypes = new HashSet<>();
		CheckGroup<ResourceGroup> group = new CheckGroup<ResourceGroup>("group", selectedTypes);
		form.add(group);
		ListView<ResourceGroup> list = new ListView<ResourceGroup>("resourceTypes", types) {

			@Override
			protected void populateItem(ListItem<ResourceGroup> item)
			{
				Check<ResourceGroup> check = new Check<ResourceGroup>("checkbox", item.getModel());
				item.add(check);
				item.add(new AttributeModifier("for", new Model<String>(check.getMarkupId())));
				item.setEscapeModelStrings(false);
				item.add(new Label("title", item.getModelObject().getTitle()));
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