/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: RelationManagerIterator.java,v 1.5 2004-05-06 12:34:41 keesj Exp $
 */
public interface RelationManagerIterator extends NodeManagerIterator {

    /**
     * Returns the next element in the iterator as a RelationManager
     */
    public RelationManager nextRelationManager();

    /**
     * Returns the previous element in the iterator as a RelationManager
     * @since MMBase-1.7
     */
    public RelationManager previousRelationManager();

}
