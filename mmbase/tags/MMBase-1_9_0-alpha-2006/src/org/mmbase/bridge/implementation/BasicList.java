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
 * @version $Id: BasicList.java,v 1.21 2006-09-25 13:58:29 michiel Exp $
 */
public class BasicList<E> extends ArrayList<E> implements BridgeList<E>  {

    private static final Logger log = Logging.getLoggerInstance(BasicList.class);

    private Map properties = new HashMap();

    // during inititializion of the list, you sometimes want to switch off
    // also when everything is certainly converted
    boolean autoConvert = true;

    BasicList() {
         super();
    }

    protected BasicList(Collection c) {
         super(c);
    }

    public Object getProperty(Object key) {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value) {
        properties.put(key,value);
    }

    /*
     * converts the object in the list to the excpected format
     */
    protected E convert(Object o, int index) {
        return (E) o;
    }

    public boolean contains(Object o ) {
        // make sure every element is of the right type, ArrayList implementation does _not_ call get.
        convertAll();
        return super.contains(o);
    }

    public boolean remove(Object o) {
        // make sure every element is of the right type, otherwise 'equals' is very odd..
        convertAll();
        return super.remove(o);
    }
    public boolean removeAll(Collection c) {
        // make sure every element is of the right type, otherwise 'equals' is very odd..
        convertAll();
        return super.removeAll(c);
    }

    /*
     * validates that an object can be converted to the excpected format
     */
    protected E validate(Object o) throws ClassCastException {
        return (E) o;
    }

    public E get(int index) {
        if (autoConvert) {
            return convert(super.get(index), index);
        } else {
            return super.get(index);
        }
    }

    public void sort() {
        Collections.sort((List) this); // casting, why?
    }

    public void sort(Comparator<? super E> comparator) {
        Collections.sort(this, comparator);
    }

    public E set(int index, E o) {
        return super.set(index,validate(o));
    }

    public void add(int index, E o) {
        autoConvert = true;
        super.add(index,validate(o));
    }

    public boolean add(E o) {
        autoConvert = true;
        return super.add(validate(o));
    }

    /**
     * @since MMBase-1.6.2
     */
    protected void convertAll() {
        log.debug("convert all");
        for (int i = 0; i < size(); i++) {
            convert(super.get(i), i);
        }
        autoConvert = false;
    }


    public Object[] toArray() { // needed when you e.g. want to sort the list.
        // make sure every element is of the right type, otherwise sorting can happen on the wrong type.
        if (autoConvert) convertAll();
        return super.toArray();
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
            BasicList.this.autoConvert = true;
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
