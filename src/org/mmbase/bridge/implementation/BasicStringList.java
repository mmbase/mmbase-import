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
 *
 * @author Pierre van Rooden
 * @version $Id: BasicStringList.java,v 1.5 2002-09-23 14:31:04 pierre Exp $
 */
public class BasicStringList extends BasicList implements StringList {
    private static Logger log = Logging.getLoggerInstance(BasicStringList.class.getName());

    BasicStringList() {
        super();
    }

    BasicStringList(Collection c) {
        super(c);
    }

    protected Object validate(Object o) throws ClassCastException {
        return (String)o;
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

        public String nextString() {
            return (String)next();
        }
    }
}
