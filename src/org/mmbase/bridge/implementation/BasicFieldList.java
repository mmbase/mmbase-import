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
 * A list of nodes
 *
 * @author Pierre van Rooden
 */
public class BasicFieldList extends BasicList implements FieldList {
    private static Logger log = Logging.getLoggerInstance(BasicFieldList.class.getName());

    private Cloud cloud;
    NodeManager nodemanager=null;

    /**
    * ...
    */
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

    public Field getField(int index) {
        return (Field)get(index);
    }
    
    /**
    *
    */
    public FieldIterator fieldIterator() {
        return new BasicFieldIterator(this);
    };

    
    public class BasicFieldIterator extends BasicIterator implements FieldIterator {
    
        BasicFieldIterator(BasicList list) {
            super(list);
        }

        
        public void set(Object o) {
            if (! (o instanceof Field)) {
                String message;
                message = "Object must be of type Field.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.set(index, o);
        }
        public void add(Object o) {
            if (! (o instanceof Field)) {
                String message;
                message = "Object must be of type Field.";
                log.error(message);
                throw new BridgeException(message);
            }
            list.add(index, o);
        }

        public void set(Field f) {
            list.set(index, f);
        }
        public void add(Field f) {
            list.add(index, f);
        }
    
        public Field nextField() {
            return (Field) next();
        }
    
    }
}
