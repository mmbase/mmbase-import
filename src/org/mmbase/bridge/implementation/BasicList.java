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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 */
public class BasicList extends AbstractList implements List {

    // array with nodes.
    // this approach is possible because the NodeList is read-only
    protected Object[] objects;

    /**
    * ...
    */
    BasicList(Collection c) {
        objects=c.toArray();
    }

    public Object getObject(int index) {
	    try {
    	    return objects[index];
    	} catch (Exception e) {
    	    throw new BridgeException("List : Invalid list index");
    	}
    }

    public Object get(int index) {
        return getObject(index);
    }

	/**
	*
	*/
    public int size() {
     return objects.length;
    }

	public class BasicIterator implements Iterator {
	    BasicList list;
	    int index=-1;
	
	    BasicIterator(BasicList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public void remove() {
	        throw new UnsupportedOperationException("Cannot remove from this list");
	    }
	
	    public Object nextObject() {
	        index++;
	        if (index>=list.size()) {
	            index = list.size()+1;
	            throw new NoSuchElementException("Object does not exits in this list");
	        } else {
    	        return list.get(index);
    	    }
	    }
	
	    public Object next() {
	        return nextObject();
	    }
	
	}
    	
}
