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
public class BasicRelationList extends BasicList implements RelationList {

    private Cloud cloud;
    NodeManager nodemanager=null;

    /**
    * ...
    */
    BasicRelationList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c);
        this.cloud=cloud;
        this.nodemanager=nodemanager;
    }

    /**
	*
	*/
	public Relation get(int index) {
	    Object o=getObject(index);
    	if (o instanceof Relation) {
    	    return (Relation)o;
    	}
    	Relation r = new BasicRelation((MMObjectNode)o,nodemanager);
    	objects[index]=r;
    	return r;
	}

	/**
	*
	*/
//	public RelationIterator iterator() {
//	    return new BasicRelationIterator(this);
//	};

	public class BasicRelationIterator { // implements RelationIterator {
	    RelationList list;
	    int index=-1;
	
	    BasicRelationIterator(RelationList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public Relation next() {
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
