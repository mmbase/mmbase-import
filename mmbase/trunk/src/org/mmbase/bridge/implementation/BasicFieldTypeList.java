/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.corebuilders.*;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 */
public class BasicFieldTypeList extends BasicList implements FieldTypeList {

    private Cloud cloud;
    NodeManager nodemanager=null;

    /**
    * ...
    */
    BasicFieldTypeList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c);
        this.cloud=cloud;
        this.nodemanager=nodemanager;
    }

    /**
	*
	*/
	public FieldType get(int index) {
    	Object o=getObject(index);
    	if (o instanceof FieldType) {
    	    return (FieldType)o;
    	}
    	FieldType ft = new BasicFieldType((FieldDefs)o,nodemanager);
    	objects[index]=ft;
        return ft;
	}

	/**
	*
	*/
//	public FieldTypeIterator iterator() {
//	    return new BasicFieldTypeIterator(this);
//	};

	
	public class BasicFieldTypeIterator { // implements FieldTypeIterator {
	    FieldTypeList list;
	    int index=-1;
	
	    BasicFieldTypeIterator(FieldTypeList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public FieldType next() {
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
