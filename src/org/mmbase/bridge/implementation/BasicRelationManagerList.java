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
 * A list of relation managers
 *
 * @author Pierre van Rooden
 */
public class BasicRelationManagerList extends BasicList implements RelationManagerList {

    private Cloud cloud;
    RelationManager relationManager=null;

    /**
    * ...
    */
    BasicRelationManagerList(Collection c, Cloud cloud) {
        super(c);
        this.cloud=cloud;
    }

    /**
	*
	*/
	public RelationManager get(int index) {
	    Object o=getObject(index);
    	if (o instanceof RelationManager) {
    	    return (RelationManager)o;
    	}
    	RelationManager rm = new BasicRelationManager((MMObjectNode)o,cloud);
    	objects[index]=rm;
    	return rm;
	}

	/**
	*
	*/
//	public RelationManagerIterator iterator() {
//	    return new BasicRelationManagerIterator(this);
//	};
	
	public class BasicRelationManagerIterator { // implements RelationManagerIterator {
	    RelationManagerList list;
	    int index=-1;
	
	    BasicRelationManagerIterator(RelationManagerList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public RelationManager next() {
	        index++;
	        if (index>=list.size()) {
	            index = list.size()+1;
	            throw new NoSuchElementException("RelationManager does not exits in this list");
	        } else {
    	        return list.get(index);
    	    }
	    }
	
	}
	
}
