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
public class BasicNodeList extends BasicList implements NodeList {

    private Cloud cloud;
    NodeManager nodemanager=null;

    /**
    * ...
    */
    BasicNodeList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c);
        this.cloud=cloud;
        this.nodemanager=nodemanager;
    }

    BasicNodeList(Collection c, Cloud cloud) {
        this(c,cloud,null);
    }

    /**
	*
	*/
	public Node get(int index) {
    	Object o=getObject(index);
    	if (o instanceof Node) {
    	    return (Node)o;
    	}
    	MMObjectNode mmn= (MMObjectNode)o;
    	NodeManager nm = nodemanager;
    	if (nm==null) {
            nm=cloud.getNodeManager(mmn.parent.getTableName());
        }
    	Node n=new BasicNode(mmn,nm);
    	objects[index]=n;
        return n;
	}

	public NodeList subList(int from, int max) {
	    return new BasicNodeList(getObjects(from,max),cloud,nodemanager);
	};
	
	/**
	*
	*/
	public NodeIterator iterator() {
	    return new BasicNodeIterator(this);
	};

	public class BasicNodeIterator implements NodeIterator {
	    NodeList list;
	    int index=-1;
	
	    BasicNodeIterator(NodeList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public Node next() {
	        index++;
	        if (index>=list.size()) {
	            index = list.size()+1;
	            throw new NoSuchElementException("Node does not exits in this list");
	        } else {
    	        return list.get(index);
    	    }
	    }
	
	}
	
}
