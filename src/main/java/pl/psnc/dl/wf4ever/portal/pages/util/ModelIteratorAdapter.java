/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.util;

import java.util.Iterator;

import org.apache.wicket.model.IModel;

/**
 * Improved version of Wicket's class.
 * 
 * @param <T>
 *            class of objects over which to iterate
 * 
 * @author piotrhol
 */
public abstract class ModelIteratorAdapter<T> implements Iterator<IModel<T>> {

    /** Don't remember what that was... */
    private final Iterator<? extends T> delegate;


    /**
     * Constructor.
     * 
     * @param delegate
     *            iterator that will be wrapped
     */
    public ModelIteratorAdapter(Iterator<? extends T> delegate) {
        this.delegate = delegate;
    }


    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }


    @Override
    public IModel<T> next() {
        return model(delegate.next());
    }


    @Override
    public void remove() {
        delegate.remove();
    }


    /**
     * This method is used to wrap the provided object with an implementation of IModel. The provided object is
     * guaranteed to be returned from the delegate iterator.
     * 
     * @param object
     *            object to be wrapped
     * @return IModel wrapper for the object
     */
    protected abstract IModel<T> model(T object);
}
