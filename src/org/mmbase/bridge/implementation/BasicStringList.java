/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import java.util.NoSuchElementException;
import org.mmbase.bridge.StringList;
import org.mmbase.bridge.StringIterator;
import org.mmbase.bridge.BridgeException;
import org.mmbase.util.logging.*;

/**
 * A list of Clouds
 */
public class BasicStringList extends BasicList implements StringList {
    private static Logger log = Logging.getLoggerInstance(BasicStringList.class.getName());

    BasicStringList(Collection c) {
    	super(c);
    }


    public String getString(int index) {
    	return (String)get(index);
    }
	
    public StringIterator stringIterator() {
	return new BasicStringIterator(this);
    }
	
    public class BasicStringIterator extends BasicIterator implements StringIterator {
        BasicStringIterator(BasicList list) {
            super(list);
        }

        public void set(Object o) {
            if (! (o instanceof String)) {
                String message;
                message = "Object must be of type String.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.set(index, o);
        }

        public void add(Object o) {
            if (! (o instanceof String)) {
                String message;
                message = "Object must be of type String.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.add(index, o);
        }

        public void set(String s) {
            list.set(index, s);
        }

        public void add(String s) {
            list.add(index, s);
        }

	
    	public String nextString() {
	    return (String)next();
    	}
    }	
}
