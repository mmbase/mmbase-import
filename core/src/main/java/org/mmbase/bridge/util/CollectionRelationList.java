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
 * A list of {@link org.mmbase.bridge.Relation}s, based on a Collection of Nodes
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class CollectionRelationList extends AbstractCollectionNodeList<Relation> implements RelationList {

    private static final Logger log = Logging.getLoggerInstance(CollectionRelationList.class);


    public CollectionRelationList(Collection<? extends Node> c, NodeManager nodeManager) {
        super(c, nodeManager);
    }


    public CollectionRelationList(Collection<? extends Node> c, Cloud cloud) {
        super(c, cloud);
    }

    public Relation getRelation(int index) {
        return get(index);
    }

    public CollectionRelationList subList(int fromIndex, int toIndex)  {
        return subRelationList(fromIndex, toIndex);
    }

    public CollectionRelationList subRelationList(int fromIndex, int toIndex) {
        if (nodeManager != null) {
            return new CollectionRelationList(subList(fromIndex, toIndex), nodeManager);
        } else {
            return new CollectionRelationList(subList(fromIndex, toIndex), cloud);
        }
    }

    public RelationIterator relationIterator() {
        return new BasicRelationIterator();
    }

    protected class BasicRelationIterator extends BasicIterator implements RelationIterator {

        public Relation nextRelation() {
            return next();
        }

        public Relation previousRelation() {
            return previous();
        }
    }
}
