/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import org.mmbase.bridge.StringList;
import org.mmbase.bridge.StringIterator;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A list of Clouds
 */
public class BasicStringList extends BasicList implements StringList {

    BasicStringList(Collection c) {
    	super(c);
    }


    public String getString(int index) {
    	return (String)getObject(index);
    }
	
    public StringIterator stringIterator() {
	return new BasicStringIterator(this);
    }
	
    public class BasicStringIterator extends BasicIterator implements StringIterator {
    	    BasicStringIterator(BasicList list) {
    	    	super(list);
	    }
	
    	public String nextString() {
	    return (String)nextObject();
    	}
    }	
}
