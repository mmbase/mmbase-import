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
 * @version $Id$
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
        } else if (o instanceof Field) {
            // core-field does not have a node-manager, fix that.
            Field f = new BasicField((Field)o, nodemanager);
            set(index, f);
            return f;
        } else { // give it up
            // perhaps we could anticipated DataType, String those kind of things too.
            // but this is not used at the moment anyway.
            return o;
        }
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
