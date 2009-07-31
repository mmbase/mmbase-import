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
 * A list of relation managers
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BasicRelationManagerList extends AbstractNodeList<RelationManager> implements RelationManagerList {

    BasicRelationManagerList() {
        super();
    }

    BasicRelationManagerList(Collection c, Cloud cloud) {
        super(c, cloud);
    }

    public RelationManager getRelationManager(int index) {
        return get(index);
    }

    public RelationManagerIterator relationManagerIterator() {
        return new BasicRelationManagerIterator();
    }

    public RelationManagerList subRelationManagerList(int fromIndex, int toIndex) {
        return new BasicRelationManagerList(subList(fromIndex, toIndex), cloud);
    }

    protected class BasicRelationManagerIterator extends BasicIterator implements RelationManagerIterator {

        public RelationManager nextRelationManager() {
            return next();
        }
        public RelationManager previousRelationManager() {
            return previous();
        }
    }

}
