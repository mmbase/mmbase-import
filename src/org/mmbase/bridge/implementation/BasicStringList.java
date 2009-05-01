/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import org.mmbase.bridge.StringList;
import org.mmbase.bridge.StringIterator;

/**
 * A list of Strings
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicStringList extends BasicList<String> implements StringList {

    static final StringList EMPTY = org.mmbase.bridge.util.BridgeCollections.EMPTY_STRINGLIST;

    BasicStringList() {
        super();
    }

    BasicStringList(Collection<String> c) {
        super(c);
    }

    public String getString(int index) {
        return get(index);
    }

    public StringIterator stringIterator() {
        return new BasicStringIterator();
    }

    protected class BasicStringIterator extends BasicIterator implements StringIterator {

        public String nextString() {
            return next();
        }

        public String previousString() {
            return previous();
        }
    }
}
