/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.util;

import java.util.ArrayList;
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

/**
 * @author piotrhol
 * @param <T>
 * 
 */
public abstract class SelectableRefreshableView<T> extends RefreshingView<T> {

    private static final long serialVersionUID = -6801474007329856060L;

    private T selectedObject;


    public SelectableRefreshableView(String id, IModel<? extends List<? extends T>> model) {
        super(id, model);
    }


    public SelectableRefreshableView(String id) {
        super(id);
    }


    @Override
    protected void populateItem(final Item<T> item) {
        item.add(new AjaxEventBehavior("onclick") {

            private static final long serialVersionUID = 1L;


            @Override
            protected void onEvent(AjaxRequestTarget target) {
                selectedObject = item.getModelObject();
                onSelectItem(target, item);
            }
        });
        // do distinguish between selected and unselected rows we add an
        // behavior that modifies row css class.
        item.add(new Behavior() {

            private static final long serialVersionUID = 1L;


            /**
             * @see org.apache.wicket.behavior.AbstractBehavior#onComponentTag(org.apache.wicket. Component,
             *      org.apache.wicket.markup.ComponentTag)
             */
            @Override
            public void onComponentTag(final Component component, final ComponentTag tag) {
                super.onComponentTag(component, tag);
                if (item.getModelObject().equals(getSelectedObject())) {
                    tag.put("class", "selected");
                }
            }
        });
    }


    @Override
    protected Iterator<IModel<T>> getItemModels() {
        @SuppressWarnings("unchecked")
        IModel<? extends List<? extends T>> model = (IModel<? extends List<? extends T>>) getDefaultModel();
        List<? extends T> source = (model.getObject() != null) ? model.getObject() : new ArrayList<T>();
        return new ModelIteratorAdapter<T>(source.iterator()) {

            @Override
            protected IModel<T> model(T stmt) {
                return new CompoundPropertyModel<T>(stmt);
            }
        };
    }


    /**
     * @return the selectedItem
     */
    public T getSelectedObject() {
        return selectedObject;
    }


    /**
     * @param selectedObject
     *            the selectedObject to set
     */
    public void setSelectedObject(T selectedObject) {
        this.selectedObject = selectedObject;
    }


    public abstract void onSelectItem(AjaxRequestTarget target, Item<T> item);

}
