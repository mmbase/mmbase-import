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
 * @version $Id: RelationIterator.java,v 1.5 2004-05-06 12:34:40 keesj Exp $
 */
public interface RelationIterator extends NodeIterator {

    /**
     * Returns the next element in the iterator as a Relation
     */
    public Relation nextRelation();

    /**
     * Returns the previous element in the iterator as a Relation
     * @since MMBase-1.7
     */
    public Relation previousRelation();

}
