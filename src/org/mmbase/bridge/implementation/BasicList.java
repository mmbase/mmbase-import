/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 */
public class BasicList {

    // array with nodes.
    // this approach is possible because the NodeList is read-only
    protected Object[] objects;

    /**
    * ...
    */
    BasicList(Collection c) {
        objects=c.toArray();
    }

    protected Object getObject(int index) {
	    try {
    	    return objects[index];
    	} catch (Exception e) {
    	    throw new BridgeException("List : Invalid list index");
    	}
    }

	protected List getObjects(int index, int max) {
	    List ls=Arrays.asList(objects);
	    return ls.subList(index,index+max);
    }

	/**
	*
	*/
    public int size() {
     return objects.length;
    }
	
	/**
	*
	*/
    public boolean isEmpty() {
     return size()==0;
    }
	
}
