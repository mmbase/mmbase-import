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
 * A list of relations
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicRelationList extends AbstractNodeList<Relation> implements RelationList {

    BasicRelationList() {
        super();
    }

    BasicRelationList(Collection c, Cloud cloud) {
        super(c, cloud);
    }

    BasicRelationList(Collection c, NodeManager nodemanager) {
        super(c, nodemanager);
    }


    public Relation getRelation(int index) {
        return get(index);
    }

    public RelationList subRelationList(int fromIndex, int toIndex) {
        if (nodeManager != null) {
            return new BasicRelationList(subList(fromIndex, toIndex), nodeManager);
        } else {
            return new BasicRelationList(subList(fromIndex, toIndex), cloud);
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
