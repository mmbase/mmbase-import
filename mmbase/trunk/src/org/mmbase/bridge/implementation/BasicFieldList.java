/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import org.mmbase.bridge.*;
import org.mmbase.core.CoreField;

/**
 * A list of fields
 *
 * @author Pierre van Rooden
 * @version $Id: BasicFieldList.java,v 1.17 2005-12-29 19:14:05 michiel Exp $
 */
public class BasicFieldList extends BasicList implements FieldList {

    NodeManager nodemanager=null;

    BasicFieldList() {
        super();
    }

    public BasicFieldList(Collection c, NodeManager nodemanager) {
        super(c);
        this.nodemanager = nodemanager;
    }

    public Object convert(Object o, int index) {
        if (o instanceof BasicField) {
            return o;
        }
        Field f = new BasicField((CoreField)o,nodemanager);
        set(index, f);
        return f;
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof CoreField) {
            return o;
        } else {
            return (Field)o;
        }
    }

    public Field getField(int index) {
        return (Field)get(index);
    }

    public FieldIterator fieldIterator() {
        return new BasicFieldIterator();
    }

    protected class BasicFieldIterator extends BasicIterator implements FieldIterator {

        public Field nextField() {
            return (Field) next();
        }

        public Field previousField() {
            return (Field) previous();
        }

    }
}
