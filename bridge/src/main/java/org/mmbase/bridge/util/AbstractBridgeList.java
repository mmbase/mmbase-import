/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import java.util.*;

/**
 * As AbstractList, but implements some extra methods required by BridgeList
 *
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

abstract public class AbstractBridgeList<E extends Comparable<? super E>> extends AbstractList<E> implements BridgeList<E> {

    private Map<Object, Object> properties = new HashMap<Object, Object>();

    // javadoc inherited
    @Override
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    // javadoc inherited
    @Override
    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }

    @Override
    public Map<Object, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    // javadoc inherited
    @Override
    public void sort() {
        Collections.sort(this);
    }

    // javadoc inherited
    @Override
    public void sort(Comparator<? super E> comparator) {
        Collections.sort(this, comparator);
    }
    @Override
    public abstract BridgeList<E> subList(int f, int t);

    protected class BasicIterator implements ListIterator<E> {
        protected final ListIterator<E> iterator;

        protected BasicIterator() {
            this.iterator = AbstractBridgeList.this.listIterator();
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
            return iterator.next();
        }
        @Override
        public E previous() {
            return iterator.previous();
        }

    }


}
