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

abstract public class AbstractBridgeList extends AbstractList implements BridgeList {

    private Map properties = new HashMap();

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
    public void sort(Comparator comparator) {
        Collections.sort(this, comparator);
    }

    protected class BasicIterator implements ListIterator {
        protected ListIterator iterator;

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
        public void set(Object o) {
            iterator.set(o);
        }

        public void add(Object o) {
            iterator.add(o);
        }

        public Object next() {
            return iterator.next();
        }
        public Object previous() {
            return iterator.previous();
        }

    }


}
