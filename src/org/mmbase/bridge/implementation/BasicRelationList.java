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
 * @version $Id: BasicRelationList.java,v 1.8 2002-09-23 14:31:04 pierre Exp $
 */
public class BasicRelationList extends BasicNodeList implements RelationList {
    private static Logger log = Logging.getLoggerInstance(BasicRelationList.class.getName());

    /**
     * ...
     */
    BasicRelationList(Cloud cloud) {
        super(cloud);
    }

    /**
     * ...
     */
    BasicRelationList(Collection c, Cloud cloud, NodeManager nodemanager) {
        super(c,cloud,nodemanager);
    }
    
    protected Object validate(Object o) throws ClassCastException {
        if (o instanceof MMObjectNode) {
            if (((MMObjectNode) o).getBuilder() instanceof org.mmbase.module.corebuilders.InsRel) {
                return o;
            } else {
                throw new ClassCastException("not a relation node");
            }
        } else {
            return (Relation)o;
        }
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

    public class BasicRelationIterator extends BasicNodeIterator implements RelationIterator {        
        BasicRelationIterator(BasicList list) {
            super(list);
        }

        public Relation nextRelation() {
            return (Relation)next();
        }
    }
}
