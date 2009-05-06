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

    private Map<Object,Object> properties = new HashMap<Object,Object>();

    // javadoc inherited
    public Object getProperty(Object key) {
        return properties.get(key);
    }

    // javadoc inherited
    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }

    // javadoc inherited
    public void sort() {
        Collections.sort(this);
    }

    // javadoc inherited
    public void sort(Comparator<? super E> comparator) {
        Collections.sort(this, comparator);
    }
    public abstract BridgeList<E> subList(int f, int t);

    protected class BasicIterator implements ListIterator<E> {
        protected final ListIterator<E> iterator;

        protected BasicIterator() {
            this.iterator = AbstractBridgeList.this.listIterator();
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
            return iterator.next();
        }
        public E previous() {
            return iterator.previous();
        }

    }


}
