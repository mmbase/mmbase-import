/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.Collection;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;

/**
 * A list of relations
 *
 * @author Pierre van Rooden
 * @version $Id: BasicRelationList.java,v 1.19 2005-12-27 21:53:20 michiel Exp $
 */
public class BasicRelationList extends BasicNodeList implements RelationList {

    BasicRelationList() {
        super();
    }

    BasicRelationList(Collection c, Cloud cloud) {
        super(c, cloud);
    }

    BasicRelationList(Collection c, NodeManager nodemanager) {
        super(c, nodemanager);
    }

    protected Object validate(Object o) throws ClassCastException,IllegalArgumentException {
        if (o instanceof MMObjectNode) {
            if (((MMObjectNode) o).getBuilder() instanceof org.mmbase.module.corebuilders.InsRel) {
                return o;
            } else {
                throw new IllegalArgumentException("requires a relation node");
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
        return new BasicRelationList(subList(fromIndex, toIndex), nodeManager);
    }

    /**
     *
     */
    public RelationIterator relationIterator() {
        return new BasicRelationIterator();
    }

    protected class BasicRelationIterator extends BasicNodeIterator implements RelationIterator {

        public Relation nextRelation() {
            return (Relation)next();
        }

        public Relation previousRelation() {
            return (Relation)previous();
        }
    }
}
