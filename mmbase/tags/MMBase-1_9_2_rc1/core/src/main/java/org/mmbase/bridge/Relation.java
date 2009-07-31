/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A relation within the cloud.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface Relation extends Node {

    /**
     * Retrieves the source of the relation
     * @return the source node
     */
    public Node getSource();

    /**
     * Retrieves the destination of the relation
     * @return the destination node
     */
    public Node getDestination();

    /**
     * set the source of the relation
     * @param node the source node
     */
    public void setSource(Node node);

    /**
     * set the destination of the relation
     * @param node the destination node
     */
    public void setDestination(Node node);

    /**
     * Retrieves the RelationManager used
     * @return the RelationManager
     */
    public RelationManager getRelationManager();
}
