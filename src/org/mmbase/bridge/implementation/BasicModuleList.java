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
	public Module getModule(int index) {
        return (Module)getObject(index);
	}

	/**
	*
	*/
	public ModuleIterator moduleIterator() {
	    return new BasicModuleIterator(this);
	};

	public class BasicModuleIterator extends BasicIterator implements ModuleIterator {
	
	    BasicModuleIterator(BasicList list) {
	        super(list);
	    }
	
	    public Module nextModule() {
	        return (Module)nextObject();
	    }
	
	}
	
}
