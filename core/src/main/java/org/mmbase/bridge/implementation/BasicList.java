/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import java.util.*;

import org.mmbase.bridge.BridgeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

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

    public Object getProperty(Object key) {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }
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

    public void sort() {
        Collections.sort(backing, new ObjectComparator());
    }

    public void sort(Comparator<? super E> comparator) {
        Collections.sort(backing, new WrappedComparator(comparator));
    }

    protected class ObjectComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            return convert(o1).compareTo(convert(o2));
        }
    }

    protected class WrappedComparator implements Comparator<Object> {

        final Comparator<? super E> comparator;

        public WrappedComparator(Comparator<? super E> comparator) {
            this.comparator = comparator;
        }


        public int compare(Object o1, Object o2) {
            E e1 = convert(o1);
            E e2 = convert(o2);
            if (comparator == null) {
                if (e1 != null) {
                    return e1.compareTo(e2);
                } else if (e2 != null) {
                    return -1 * e2.compareTo(e1);
                } else {
                    return 0;
                }
            } else {
                return comparator.compare(e1, e2);
            }
        }
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



    public BridgeList<E> subList(int fromIndex, int toIndex)  {
        return new BasicList<E>(super.subList(fromIndex, toIndex));
    }

    protected class BasicIterator implements ListIterator<E> {
        protected ListIterator<E> iterator;

        protected BasicIterator() {
            this.iterator = BasicList.this.listIterator();
        }

        public boolean hasNext() {
            return  iterator.hasNext();
        }

        public boolean hasPrevious() {
            return  iterator.hasPrevious();
        }

        public int nextIndex() {
            return iterator.nextIndex();
        }

        public int previousIndex() {
            return iterator.previousIndex();
        }

        public void remove() {
            iterator.remove();
        }

        // These have to be implemented with a check if o is of the right type.
        public void set(E o) {
            iterator.set(o);
        }

        public void add(E o) {
            iterator.add(o);
        }

        public E next() {
            E next = iterator.next();
            int i = nextIndex();
            return BasicList.this.convert(next, i);
        }

        public E previous() {
            E previous = iterator.previous();
            int i = previousIndex();
            return BasicList.this.convert(previous, i);
        }

    }

}
