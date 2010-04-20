/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * A list of objects.
 * This is the base class for all basic implementations of the bridge lists.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class BasicList<E extends Comparable<? super E>> extends AbstractList<E> implements BridgeList<E>, java.io.Serializable, RandomAccess  {
    private static final long serialVersionUID = 5940343949744992633L;
    private static final Logger log = Logging.getLoggerInstance(BasicList.class);

    private final Map<Object, Object> properties = new HashMap<Object, Object>();

    private boolean converted = false;


    /**
     * @since MMBase-1.9.1
     */
    private final ArrayList<Object> backing;

    BasicList() {
        super();
        backing = new ArrayList<Object>();
    }

    protected BasicList(Collection c) {
        super();
        backing = new ArrayList<Object>(c);
    }
    /**
     * @since MMBase-1.9.4
     */
    public BasicList(BridgeList<? extends E> b) {
        super();
        backing = new ArrayList<Object>(b);
        properties.putAll(b.getProperties());
    }


    @Override
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    @Override
    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }
    @Override
    public Map<Object, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * converts the object in the list to the excpected format
     */
    @SuppressWarnings("unchecked")
    protected E convert(Object o) {
        return (E) o;
    }
    protected final E convert(Object o, int index) {
        E newO;
        try {
            newO = convert(o);
            if (log.isDebugEnabled()) {
                log.debug("Converted " + o.getClass() + " to " + newO.getClass() + " in " + getClass());
            }
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
            newO = null;
        }
        if (newO != o) {
            backing.set(index, newO);
        }
        return newO;
    }

    @Override
    public  E get(int i) {
        return convert(backing.get(i), i);
    }


    @Override
    public int size() {
        return backing.size();
    }
    @Override
    public E set(int i, E e) {
        return convert(backing.set(i, e));
    }
    @Override
    public void add(int i, E e) {
        backing.add(i, e);
    }
    @Override
    public E remove(int i) {
        return convert(backing.remove(i));
    }

    @Override
    public void sort() {
        Collections.sort(this);
    }

    @Override
    public void sort(Comparator<? super E> comparator) {
        Collections.sort(this, comparator);
    }


    /**
     * @since MMBase-1.6.2
     */
    protected void convertAll() {
        if (! converted) {
            log.debug("convert all");
            for (int i = 0; i < size(); i++) {
                convert(backing.get(i), i);
            }
            converted = true;
        }
    }


    @Override
    public Object[] toArray() { // needed when you e.g. want to sort the list.
        // make sure every element is of the right type, otherwise sorting can happen on the wrong type.
        convertAll();
        return backing.toArray();
    }



    @Override
    public BridgeList<E> subList(int fromIndex, int toIndex)  {
        return new BasicList<E>(super.subList(fromIndex, toIndex));
    }

    protected class BasicIterator implements ListIterator<E> {
        protected ListIterator<E> iterator;

        protected BasicIterator() {
            this.iterator = BasicList.this.listIterator();
        }

        @Override
        public boolean hasNext() {
            return  iterator.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return  iterator.hasPrevious();
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            iterator.remove();
        }

        // These have to be implemented with a check if o is of the right type.
        @Override
        public void set(E o) {
            iterator.set(o);
        }

        @Override
        public void add(E o) {
            iterator.add(o);
        }

        @Override
        public E next() {
            E next = iterator.next();
            int i = nextIndex();
            return BasicList.this.convert(next, i);
        }

        @Override
        public E previous() {
            E previous = iterator.previous();
            int i = previousIndex();
            return BasicList.this.convert(previous, i);
        }

    }

}
