/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.List;
import java.util.Collection;
import java.util.NoSuchElementException;


/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 */
public class BasicList extends ArrayList  {

    /**
    * ...
    */
    BasicList(Collection c) {
         super(c);
    }
    protected Object convert(Object o, int index) { // 'virtual' method       
        System.err.println("base");
        return o;
    }
    
    public Object get(int index) {        
        return convert(super.get(index), index);
    }

    public Object[] toArray() { // needed when you e.g. want to sort the list.
        System.err.println("hier");
        // make sure every element is of the right type, otherwise sorting can happen on the wrong type.
        for (int i = 0; i < this.size(); i++) {
            convert(super.get(i), i);
        }
        System.err.println("daar");
        return super.toArray();
    }

    // depending on how functions like 'indexOf' and 'lastIndexOf' are implemented (if they use get), 
    // perhaps also those functions must be overrided like toArray.


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
        public abstract void set(Object o); 
        public abstract void add(Object o);
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
