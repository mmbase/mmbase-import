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

    private Cloud cloud;
    NodeManager nodemanager=null;

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
	public NodeManager get(int index) {
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
//	public NodeManagerIterator iterator() {
//	    return new BasicNodeManagerIterator(this);
//	};
	
	public class BasicNodeManagerIterator { // implements NodeManagerIterator {
	    NodeManagerList list;
	    int index=-1;
	
	    BasicNodeManagerIterator(NodeManagerList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public NodeManager next() {
	        index++;
	        if (index>=list.size()) {
	            index = list.size()+1;
	            throw new NoSuchElementException("NodeManager does not exits in this list");
	        } else {
    	        return list.get(index);
    	    }
	    }
	
	}
	
}
