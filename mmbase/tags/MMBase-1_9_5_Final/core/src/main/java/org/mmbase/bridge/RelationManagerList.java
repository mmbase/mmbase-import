/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of Relation Managers
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface RelationManagerList extends BridgeList<RelationManager> {

    /**
     * Returns the RelationManager at the indicated postion in the list
     * @param index the position of the RelationManager to retrieve
     * @return RelationManager at the indicated postion
     */
    public RelationManager getRelationManager(int index);

    /**
     * Returns an type-specific iterator for this list.
     * @return RelationManager iterator
     */
    public RelationManagerIterator relationManagerIterator();

}
