/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * A list of objects.
 * This is the base class for all basic implementations of the bridge lists.
 *
 * @author Pierre van Rooden
 * @version $Id: BasicList.java,v 1.8 2002-09-23 14:31:03 pierre Exp $
 */
public class BasicList extends ArrayList implements BridgeList  {

    private static Logger log = Logging.getLoggerInstance(BasicList.class.getName());
  
    BasicList() {
         super();
    }
    
    BasicList(Collection c) {
         super(c);
    }

    /*
     * converts the object in teh list to the excpected format  
     */    
    protected Object convert(Object o, int index) {
        return o;
    }

    /*
     * validates that an object can be converted to the excpected format  
     */    
    protected Object validate(Object o) throws ClassCastException {
        return o;
    }
    
    public Object get(int index) {
        return convert(super.get(index), index);
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
        super.add(index,validate(o));
    }

    public boolean add(Object o) {
        return super.add(validate(o));
    }

    public Object[] toArray() { // needed when you e.g. want to sort the list.
        // make sure every element is of the right type, otherwise sorting can happen on the wrong type.
        for (int i = 0; i < this.size(); i++) {
            convert(super.get(i), i);
        }
        return super.toArray();
    }

    public abstract class BasicIterator implements ListIterator {        
        protected BasicList list;
        protected int index=-1;

        BasicIterator(BasicList list) {
            this.list = list;
        }

        public boolean hasNext() {
            return  index < (list.size()-1);
        }

        public boolean hasPrevious() {
            return index > 0;
        }

        public int nextIndex() {
            return index + 1;
        }
        public int previousIndex() {
            return index - 1;
        }

        public void remove() {
            list.remove(index);
        }

        // These have to be implemented with a check if o is of the right type.
        public void set(Object o) {
            list.set(index, o);
        }

        public void add(Object o) {
            list.add(index, o);
        }
        
        // normally also e.g. set(Node n); and add(Node n) will be created in
        // descendant class, because that is better for performance.

        public Object next() {
            index++;
            if (index>=list.size()) {
                index = list.size()+1;
                throw new NoSuchElementException("Object does not exist in this list");
            } else {
                return list.get(index);
            }
        }

        public Object previous() {
            index--;
            if (index < 0) {
                throw new NoSuchElementException("Object does not exist in this list");
            }
            return list.get(index);
        }

    }

}
