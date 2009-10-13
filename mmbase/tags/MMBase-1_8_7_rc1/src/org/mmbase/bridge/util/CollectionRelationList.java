/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * A list of nodes, based on a Collection of Nodes
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class CollectionRelationList extends CollectionNodeList implements RelationList {

    private static final Logger log = Logging.getLoggerInstance(CollectionRelationList.class);


    public CollectionRelationList(Collection c, NodeManager nodeManager) {
        super(c, nodeManager);
    }


    public CollectionRelationList(Collection c, Cloud cloud) {
        super(c, cloud);
    }

    protected Object validate(Object o) throws ClassCastException,IllegalArgumentException {
        return (Relation)o;
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
        if (nodeManager != null) {
            return new CollectionRelationList(subList(fromIndex, toIndex), nodeManager);
        } else {
            return new CollectionRelationList(subList(fromIndex, toIndex), cloud);
        }
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
