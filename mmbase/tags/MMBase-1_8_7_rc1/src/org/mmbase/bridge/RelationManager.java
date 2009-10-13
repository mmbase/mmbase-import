/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * This interface represents a relation constraint (or contex, if you like).
 * More specifically, it represents a relation manager (itself a node manager) as it applies between bnode belonging to
 * two other node managers.
 * Some of the information here is retrieved from the NodeManager used to build the catual relation node
 * (the data as described in the xml builder config file). This NodeManager is also referred to as the parent.
 * Other data is retrieved from a special (hidden) object that decsribes what relations apply between two nodes.
 * (formerly known as the TypeRel builder).
 * This includes direction and cardinality, and the NodeManagers of nodes itself. These fields cannot be changed
 * except through the use of an administration module.
 * This interface is therefor not a real mmbase 'object' in itself - it exists of two objects joined together.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface RelationManager extends NodeManager {
    /**
     * Directionality constant : uni-directional
     */
    public final static int UNIDIRECTIONAL = 1;

    /**
     * Directionality constant : bi-directional
     */
    public final static int BIDIRECTIONAL = 2;

    /**
     * Retrieves the role of the source to the destination
     * @return the role as a <code>String</code>
     */
    public String getForwardRole();

    /**
     * Retrieves the role of the destination to the source
     * @return the role as a <code>String</code>
     */
    public String getReciprocalRole();

    /**
     * Retrieves the gui name (prompt) of the role from source to destination
     * @return the name as a <code>String</code>
     */
    public String getForwardGUIName();

    /**
     * Retrieves the gui name (prompt) of the role from destination to source
     * @return the name as a <code>String</code>
     */
    public String getReciprocalGUIName();

    /**
     * Retrieves the directionality for this type (the default assigned to a new relation).
     * @return one of the directionality constants
     */
    public int getDirectionality();

    /**
     * Retrieves the NodeManager of node that can act as the source of a relation of this type.
     * @return the source NodeManager
     */
    public NodeManager getSourceManager();

    /**
     * Retrieves the type of node that can act as the destination of a relation of this type.
     * @return the destination NodeManager
     */
    public NodeManager getDestinationManager();

    /**
     * Adds a relation from this type.
     * @param sourceNode the node from which you want to relate
     * @param destinationNode the node to which you want to relate
     * @return the added relation
     */
    public Relation createRelation(Node sourceNode, Node destinationNode);

    /**
     * This method from Node is redeclared here to prevent an ambiguous invocation of method.
     * reson: the the method in the base class (Node) is more specific than the one in the RelationManager
     * (RelationManager extends Node).
     * @param sourceNode source node of the relation
     * @param relationManager relation manager of the relation
     * @return new Relation
     **/
    public Relation createRelation(Node sourceNode, RelationManager relationManager);

    /**
     * Retrieves all the relations of this type from a given node.
     * @param node the node from which to give the relations
     * @return a list of relations
     */
    public RelationList getRelations(Node node);

    /**
     * Check if the current user may create a new relation of this type between
     * the specified nodes.
     * @param sourceNode source node of the relation
     * @param destinationNode destination node of the relation
     *
     * @return  Check if the current user may create a new relation of this type
     *          between the specified nodes.
     */
    public boolean mayCreateRelation(Node sourceNode, Node destinationNode);

}
