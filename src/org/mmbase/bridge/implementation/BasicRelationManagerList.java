/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.*;
import java.util.NoSuchElementException;

/**
 * A list of relation managers
 *
 * @author Pierre van Rooden
 */
public class BasicRelationManagerList extends BasicNodeManagerList implements RelationManagerList {

    /**
    * ...
    */
    BasicRelationManagerList(Collection c, Cloud cloud) {
        super(c,cloud);
    }


    /**
    *
    */
    public Object convert(Object o, int index) {
        if (o instanceof RelationManager) {
            return o;
        }
        RelationManager rm = new BasicRelationManager((MMObjectNode)o,cloud);
        set(index, rm);
        return rm;
    }
    
    /**
    *
    */
    public RelationManager getRelationManager(int index) {
        return (RelationManager) get(index);
    }

    /**
    *
    */
    public RelationManagerIterator relationManagerIterator() {
        return new BasicRelationManagerIterator(this);
    };
    
    public class BasicRelationManagerIterator extends BasicNodeManagerIterator implements RelationManagerIterator {
    
        BasicRelationManagerIterator(BasicList list) {
            super(list);
        }
                             
        public void set(Object o) {
            if (! (o instanceof RelationManager)) {
                throw new BridgeException("Object must be of type RelationManager" );
            }
            list.set(index, o);
        }
        public void add(Object o) {
            if (! (o instanceof RelationManager)) {
                throw new BridgeException("Object must be of type RelationManager" );
            }
            list.add(index, o);
        }
        
        public void set(RelationManager n) {
            list.set(index, n);
        }
        public void add(RelationManager n) {
            list.add(index, n);
        }
        // in fact also set(NodeManager) and add(NodeManager) must be overrided....

        public RelationManager nextRelationManager() {
            return (RelationManager)next();
        }
    
    }
    
}
