/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.util;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author piotrhol
 * @param <T>
 *
 */
public abstract class SelectableRefreshableView<T>
	extends RefreshingView<T>
{

	private static final long serialVersionUID = -6801474007329856060L;

	/**
	 * Used to set selectedItem when repainting
	 */
	private LoadableDetachableModel<T> selectedObjectModel;

	/**
	 * Has precedence over selectedObject
	 */
	private transient Item<T> selectedItem;


	public SelectableRefreshableView(String id, IModel< ? extends List< ? extends T>> model)
	{
		super(id, model);
		init();
	}


	public SelectableRefreshableView(String id)
	{
		super(id);
		init();
	}


	/**
	 * 
	 */
	private void init()
	{
		selectedObjectModel = new LoadableDetachableModel<T>() {

			private static final long serialVersionUID = 4657541968660868729L;


			@Override
			protected T load()
			{
				if (selectedItem != null) {
					return selectedItem.getModelObject();
				}
				else {
					return null;
				}
			}
		};
	}


	@Override
	protected void populateItem(final Item<T> item)
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
				if (item.getModelObject().equals(getSelectedObject())) {
					tag.put("class", "selected");
					selectedItem = item;
				}
			}
		});
	}


	@Override
	protected Iterator<IModel<T>> getItemModels()
	{
		@SuppressWarnings("unchecked")
		IModel< ? extends List< ? extends T>> model = (IModel< ? extends List< ? extends T>>) getDefaultModel();
		return new ModelIteratorAdapter<T>(model.getObject().iterator()) {

			@Override
			protected IModel<T> model(T stmt)
			{
				return new CompoundPropertyModel<T>(stmt);
			}
		};
	}


	/**
	 * @return the selectedItem
	 */
	public T getSelectedObject()
	{
		if (selectedItem != null) {
			T selectedObject = null;
			try {
				selectedObject = selectedItem.getModelObject();
			}
			catch (IndexOutOfBoundsException e) {
				selectedItem = null;
			}
			setSelectedObject(selectedObject);
		}
		return selectedObjectModel.getObject();
	}


	/**
	 * @param selectedObject the selectedObject to set
	 */
	public void setSelectedObject(T selectedObject)
	{
		this.selectedObjectModel.setObject(selectedObject);
	}


	/**
	 * @param target 
	 * @param item the selectedItem to set
	 */
	private void setSelectedItem(AjaxRequestTarget target, Item<T> item)
	{
		this.selectedItem = item;
		onSelectItem(target, item);
	}


	public abstract void onSelectItem(AjaxRequestTarget target, Item<T> item);

}
