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
public class BasicRelationList extends BasicNodeList implements RelationList {

    /**
    * ...
    */
    BasicRelationList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c,cloud,nodemanager);
    }

    /**
	*
	*/
	public Object get(int index) {
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
	public Relation getRelation(int index) {
    	return (Relation)get(index);
	}
	
    /**
	*
	*/
	public RelationList subRelationList(int fromIndex, int toIndex) {
	    return new BasicRelationList(subList(fromIndex, toIndex),cloud,nodemanager);
	}
	
	/**
	*
	*/
	public RelationIterator relationIterator() {
	    return new BasicRelationIterator(this);
	};

	public class BasicRelationIterator extends BasicNodeIterator implements RelationIterator {
	
	    BasicRelationIterator(BasicList list) {
	        super(list);
	    }
	
	    public Relation nextRelation() {
	        return (Relation)nextObject();
	    }
	
	}
	
}
