/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.BridgeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;

/**
 * A list of objects.
 * This is the base class for all basic implementations of the bridge lists.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class BasicList extends AbstractList implements BridgeList, java.io.Serializable, RandomAccess  {
    private static final long serialVersionUID = 5940343949744992633L;
    private static final Logger log = Logging.getLoggerInstance(BasicList.class);

    private final Map properties = new HashMap();

	private boolean converted = false;

	// during inititializion of the list, you sometimes want to switch off
    // also when everything is certainly converted
    boolean autoConvert = true;


    /**
     * @since MMBase-1.9.1
     */
    private final ArrayList backing;

    BasicList() {
        super();
        backing = new ArrayList();
    }

    protected BasicList(Collection c) {
        super();
        backing = new ArrayList(c);
    }


    public Object getProperty(Object key) {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value) {
        properties.put(key, value);
    }
    public Map getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     * converts the object in the list to the excpected format
     */
	protected Object convert(Object o) {
        return o;
    }
    protected  Object convert(Object o, int index) {
		if (! autoConvert) return o;
        Object newO;
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

    public  Object get(int i) {
		return convert(backing.get(i), i);
    }


	public int size() {
        return backing.size();
    }

    public Object set(int i, Object e) {
        return convert(backing.set(i, e));
    }
    public void add(int i, Object e) {
        backing.add(i, e);
    }
    public Object remove(int i) {
        return convert(backing.remove(i));
    }

    public void sort() {
        Collections.sort(this);
    }

    public void sort(Comparator comparator) {
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

    public Object[] toArray() { // needed when you e.g. want to sort the list.
        // make sure every element is of the right type, otherwise sorting can happen on the wrong type.
        convertAll();
        return backing.toArray();
    }



    public List subList(int fromIndex, int toIndex)  {
        return new BasicList(super.subList(fromIndex, toIndex));
    }

    protected class BasicIterator implements ListIterator {
        protected ListIterator iterator;

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
        public void set(Object o) {
            iterator.set(o);
        }

        public void add(Object o) {
            iterator.add(o);
        }

        public Object next() {
            Object next = iterator.next();
            int i = nextIndex();
            return BasicList.this.convert(next, i);
        }

        public Object previous() {
            Object previous = iterator.previous();
            int i = previousIndex();
            return BasicList.this.convert(previous, i);
        }

    }

}
