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
 * @version $Id: BasicList.java,v 1.16 2003-12-17 09:16:44 michiel Exp $
 */
public class BasicList extends ArrayList implements BridgeList  {

    private static final Logger log = Logging.getLoggerInstance(BasicList.class);

    private Map properties = new HashMap();

    // during inititializion of the list, you sometimes want to switch off 
    // also when everything is certainly converted
    boolean autoConvert = true;

    BasicList() {
         super();
    }

    BasicList(Collection c) {
         super(c);
    }

    public Object getProperty(Object key) {
        return properties.get(key);
    }

    public void setProperty(Object key, Object value) {
        properties.put(key,value);
    }

    /*
     * converts the object in teh list to the excpected format
     */
    protected Object convert(Object o, int index) {
        return o;
    }

    public boolean contains(Object o ) {
        // make sure every element is of the right type, ArrayList implementation does _not_ call get.
        convertAll();
        return super.contains(o);
    }

    /*
     * validates that an object can be converted to the excpected format
     */
    protected Object validate(Object o) throws ClassCastException {
        return o;
    }

    public Object get(int index) {
        if (autoConvert) {
            return convert(super.get(index), index);
        } else {
            return super.get(index);
        }
    }

    public void sort() {
        Collections.sort(this);
    }

    public void sort(Comparator comparator) {
        Collections.sort(this,comparator);
    }

    public Object set(int index, Object o) {
        return super.set(index,validate(o));
    }

    public void add(int index, Object o) {
        autoConvert = true;
        super.add(index,validate(o));
    }

    public boolean add(Object o) {
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

    protected abstract class BasicIterator implements ListIterator {
        protected ListIterator iterator;

        BasicIterator() {
            this.iterator = listIterator();
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
            BasicList.this.autoConvert = true;
            iterator.add(o);
        }

        // normally also e.g. set(Node n); and add(Node n) will be created in
        // descendant class, because that is better for performance.

        public Object next() {
            return iterator.next();
        }

        public Object previous() {
            return iterator.previous();
        }

    }

}
