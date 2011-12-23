/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;

/**
 * @author piotrhol
 * @param <T>
 *
 */
public abstract class SelectablePropertyListView<T>
	extends PropertyListView<T>
{

	private static final long serialVersionUID = -6801474007329856060L;

	/**
	 * Used to set selectedItem when repainting
	 */
	private T selectedObject;

	/**
	 * Has precedence over selectedObject
	 */
	private ListItem<T> selectedItem;


	public SelectablePropertyListView(String id, IModel< ? extends List< ? extends T>> model)
	{
		super(id, model);
		if (!getModelObject().isEmpty()) {
			setSelectedObject(getModelObject().get(0));
		}
		else {
			setSelectedObject(null);
		}
	}


	@Override
	protected void populateItem(final ListItem<T> item)
	{
		item.add(new AjaxEventBehavior("onclick") {

			private static final long serialVersionUID = 1L;


			@Override
			protected void onEvent(AjaxRequestTarget target)
			{
				setSelectedItem(target, item);
			}
		});
		// do distinguish between selected and unselected rows we add an
		// behavior that modifies row css class.
		item.add(new Behavior() {

			private static final long serialVersionUID = 1L;


			/**
			 * @see org.apache.wicket.behavior.AbstractBehavior#onComponentTag(org.apache.wicket.
			 *      Component, org.apache.wicket.markup.ComponentTag)
			 */
			@Override
			public void onComponentTag(final Component component, final ComponentTag tag)
			{
				super.onComponentTag(component, tag);
				if (getSelectedObject() == item.getModelObject()) {
					tag.put("class", "selected");
					selectedItem = item;
				}
				else {
					//					tag.remove("class");
				}
			}
		});
	}


	/**
	 * @return the selectedItem
	 */
	public T getSelectedObject()
	{
		if (selectedItem != null) {
			selectedObject = selectedItem.getModelObject();
			if (selectedObject == null) {
				selectedItem = null;
			}
		}
		return selectedObject;
	}


	/**
	 * @param selectedObject the selectedObject to set
	 */
	public void setSelectedObject(T selectedObject)
	{
		this.selectedObject = selectedObject;
	}


	/**
	 * @param target 
	 * @param item the selectedItem to set
	 */
	private void setSelectedItem(AjaxRequestTarget target, ListItem<T> item)
	{
		this.selectedItem = item;
		onSelectItem(target, item);
	}


	public abstract void onSelectItem(AjaxRequestTarget target, ListItem<T> item);
}
