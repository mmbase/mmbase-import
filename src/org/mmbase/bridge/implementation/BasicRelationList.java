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
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * A list of relations
 *
 * @author Pierre van Rooden
 * @version $Id: BasicRelationList.java,v 1.7 2002-06-17 10:49:47 eduard Exp $
 */
public class BasicRelationList extends BasicNodeList implements RelationList {
    private static Logger log = Logging.getLoggerInstance(BasicRelationList.class.getName());

    /**
    * ...
    */
    BasicRelationList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c,cloud,nodemanager);
    }
    
    /**
    *
    */
    public Relation getRelation(int index) {
        return (Relation)get(index);
    }

    /**
    *
    */
    public RelationList subRelationList(int fromIndex, int toIndex) {
        return new BasicRelationList(subList(fromIndex, toIndex),cloud,nodemanager);
    }

    /**
    *
    */
    public RelationIterator relationIterator() {
        return new BasicRelationIterator(this);
    }

    public class BasicRelationIterator extends BasicIterator implements RelationIterator {        
        BasicRelationIterator(BasicList list) {
            super(list);
        }
            
        public void set(Relation n) {
            list.set(index, n);
        }
        public void add(Relation n) {
            list.add(index, n);
        }

        public Node nextNode() {
            return nextRelation();
        }

        public Relation nextRelation() {
            return (Relation)next();
        }
    }
}
