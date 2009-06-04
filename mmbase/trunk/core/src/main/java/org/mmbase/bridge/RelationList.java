/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of Relations
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface RelationList extends BridgeList<Relation> {

    /**
     * Returns the Relation at the indicated postion in the list
     * @param index the position of the Relation to retrieve
     * @return Relation at the indicated postion
     */
    public Relation getRelation(int index);

    /**
     * Returns an type-specific iterator for this list.
     * @return Relation iterator
     */
    public RelationIterator relationIterator();

    /**
     * Returns a sublist of this list.
     * @param fromIndex the position in the current list where the sublist starts (inclusive)
     * @param toIndex the position in the current list where the sublist ends (exclusive)
     * @return sublist of this list
     */
    public RelationList subRelationList(int fromIndex, int toIndex);
}
