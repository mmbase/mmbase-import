/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import java.util.NoSuchElementException;
import org.mmbase.bridge.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * A list of fields
 *
 * @author Pierre van Rooden
 * @version $Id: BasicFieldList.java,v 1.7 2002-09-25 14:57:47 pierre Exp $
 */
public class BasicFieldList extends BasicList implements FieldList {
    private static Logger log = Logging.getLoggerInstance(BasicFieldList.class.getName());

    private Cloud cloud = null;
    NodeManager nodemanager=null;

    /**
     * ...
     */
    BasicFieldList() {
        super();
    }

    BasicFieldList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c);
        this.cloud=cloud;
        this.nodemanager=nodemanager;
    }

    /**
     *
     */
    public Object convert(Object o, int index) {
        if (o instanceof Field) {
            return o;
        }
        Field f = new BasicField((FieldDefs)o,nodemanager);
        set(index, f);
        return f;
    }

    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof FieldDefs) {
            return o;
        } else {
            return (Field)o;
        }
    }

    public Field getField(int index) {
        return (Field)get(index);
    }

    /**
     *
     */
    public FieldIterator fieldIterator() {
        return new BasicFieldIterator(this);
    }

    public class BasicFieldIterator extends BasicIterator implements FieldIterator {

        BasicFieldIterator(BasicList list) {
            super(list);
        }

        public Field nextField() {
            return (Field) next();
        }

    }
}
