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
import java.util.NoSuchElementException;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 */
public class BasicNodeManagerList extends BasicList implements NodeManagerList {

    protected Cloud cloud;

    /**
    * ...
    */
    BasicNodeManagerList(Collection c, Cloud cloud) {
        super(c);
        this.cloud=cloud;
    }

    /**
	*
	*/
	public Object get(int index) {
        Object o=getObject(index);
        if (o instanceof NodeManager) {
    	    return (NodeManager)o;
        }
    	NodeManager nm = cloud.getNodeManager((String)o);
    	objects[index]=nm;
    	return nm;
	}

    /**
	*
	*/
	public NodeManager getNodeManager(int index) {
	    return (NodeManager)get(index);
	}
	
	/**
	*
	*/
	public NodeManagerIterator nodeManagerIterator() {
	    return new BasicNodeManagerIterator(this);
	};
	
	public class BasicNodeManagerIterator extends BasicIterator implements NodeManagerIterator {
	
	    BasicNodeManagerIterator(BasicList list) {
	        super(list);
	    }
	
	    public NodeManager nextNodeManager() {
	        return (NodeManager)nextObject();
	    }
	
	}
	
}
