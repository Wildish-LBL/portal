/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Form;

/**
 * @author piotrhol
 * 
 */
public abstract class MyAjaxButton
	extends AjaxButton
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6819868729651344345L;


	@SuppressWarnings("serial")
	public MyAjaxButton(String id, Form< ? > form)
	{
		super(id, form);
		add(new Behavior() {

			@Override
			public void onComponentTag(Component component, ComponentTag tag)
			{
				super.onComponentTag(component, tag);
				if (!component.isEnabled()) {
					tag.append("class", "disabled", " ");
				}
			}
		});

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.ajax.markup.html.form.AjaxButton#onSubmit(org.apache.wicket.ajax
	 * .AjaxRequestTarget, org.apache.wicket.markup.html.form.Form)
	 */
	@Override
	protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
	{
		target.appendJavaScript("hideBusy()");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.wicket.ajax.markup.html.form.AjaxButton#onError(org.apache.wicket.ajax
	 * .AjaxRequestTarget, org.apache.wicket.markup.html.form.Form)
	 */
	@Override
	protected void onError(AjaxRequestTarget target, Form< ? > form)
	{
		target.appendJavaScript("hideBusy()");
	}


	@Override
	protected IAjaxCallDecorator getAjaxCallDecorator()
	{
		return new AjaxCallDecorator() {

			private static final long serialVersionUID = 3361600615366656231L;


			@Override
			public CharSequence decorateScript(Component c, CharSequence script)
			{
				return "showBusy(); " + script;
			}
		};
	}
}
