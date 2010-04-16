/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import org.mmbase.bridge.*;

/**
 * A list of fields
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicFieldList extends BasicList<Field> implements FieldList {
    private static final long serialVersionUID = 0L;
    NodeManager nodemanager = null;

    public BasicFieldList() {
        super();
    }

    public BasicFieldList(Collection c, NodeManager nodemanager) {
        super(c);
        this.nodemanager = nodemanager;
    }

    @Override
    protected Field convert(Object o) {
        if (o instanceof BasicField) {
            return (Field) o;
        } else if (o instanceof Field) {
            // core-field does not have a node-manager, fix that.
            Field f = new BasicField((Field)o, nodemanager);
            return f;
        } else { // give it up
            // perhaps we could anticipated DataType, String those kind of things too.
            // but this is not used at the moment anyway.
            // shoudl not happen!
            return (Field) o;
        }
    }

    @Override
    public Field getField(int index) {
        return get(index);
    }

    @Override
    public FieldIterator fieldIterator() {
        return new BasicFieldIterator();
    }

    protected class BasicFieldIterator extends BasicIterator implements FieldIterator {

        @Override
        public Field nextField() {
            return next();
        }

        @Override
        public Field previousField() {
            return previous();
        }

    }
}
