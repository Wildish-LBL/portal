/**
 * 
 */
package pl.psnc.dl.wf4ever.portal.pages.util;

import java.util.Iterator;

import org.apache.wicket.model.IModel;

/**
 * @author piotrhol
 * 
 *         Improved version of Wicket's class
 * 
 */
public abstract class ModelIteratorAdapter<T> implements Iterator<IModel<T>> {

    private final Iterator<? extends T> delegate;


    /**
     * Constructor
     * 
     * @param delegate
     *            iterator that will be wrapped
     */
    public ModelIteratorAdapter(Iterator<? extends T> delegate) {
        this.delegate = delegate;
    }


    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }


    /**
     * @see java.util.Iterator#next()
     */
    @Override
    public IModel<T> next() {
        return model(delegate.next());
    }


    /**
     * @see java.util.Iterator#remove()
     */
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
