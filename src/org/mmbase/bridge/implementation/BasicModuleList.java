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
 * A list of Modules
 *
 * @author Pierre van Rooden
 */
public class BasicModuleList extends BasicList implements ModuleList {

    private CloudContext cloudcontext;

    /**
    * ...
    */
    BasicModuleList(Collection c, CloudContext cloudcontext) {
        super(c);
        this.cloudcontext=cloudcontext;
    }

    /**
	*
	*/
	public Module get(int index) {
        return (Module)getObject(index);
	}

	/**
	*
	*/
//	public ModuleIterator iterator() {
//	    return new BasicModuleIterator(this);
//	};

	public class BasicModuleIterator { // implements ModuleIterator {
	    ModuleList list;
	    int index=-1;
	
	    BasicModuleIterator(ModuleList list) {
	        this.list = list;
	    }
	
	    public boolean hasNext() {
	        return  index<(list.size()-1);
	    }
	
	    public Module next() {
	        index++;
	        if (index>=list.size()) {
	            index =list.size()+1;
	            throw new NoSuchElementException("Module does not exits in this list");
	        } else {
    	        return list.get(index);
    	    }
	    }
	
	}
	
}
