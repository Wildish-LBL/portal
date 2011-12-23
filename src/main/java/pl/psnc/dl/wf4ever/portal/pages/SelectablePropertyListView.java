/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages;

import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

	private ListItem<T> selectedItem;


	public SelectablePropertyListView(String id, IModel< ? extends List< ? extends T>> model)
	{
		super(id, model);
	}


	@Override
	protected void populateItem(final ListItem<T> item)
	{
		item.add(new AjaxEventBehavior("onclick") {

			private static final long serialVersionUID = 1L;


			@Override
			protected void onEvent(AjaxRequestTarget target)
			{
				setSelectedObject(target, item);
			}
		});
	}


	/**
	 * @return the selectedItem
	 */
	public ListItem<T> getSelectedItem()
	{
		return selectedItem;
	}


	public T getSelectedObject()
	{
		if (selectedItem != null)
			return selectedItem.getModelObject();
		else
			return null;
	}


	/**
	 * @param target 
	 * @param item the selectedItem to set
	 */
	public void setSelectedObject(AjaxRequestTarget target, ListItem<T> item)
	{
		if (item != null) {
			onDeselectObject(target, item);
		}
		this.selectedItem = item;
		onSelectObject(target, item);
	}


	public abstract void onDeselectObject(AjaxRequestTarget target, ListItem<T> item);


	public abstract void onSelectObject(AjaxRequestTarget target, ListItem<T> item);
}
