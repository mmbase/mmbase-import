/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Enumeration;

/**
 * This interface represents a relation constraint (or contex, if you like).
 * More specifically, it represents a relation type (itself a node type) as it applies between two
 * other node types.
 * Some of the information here is retrieved from the node type used to build the catual relation node
 * (the data as described in the xml builder config file). This node type is also referred to as the parent type.
 * Other data is retrieved from a special (hidden) object that decsribes what relations apply between two nodes.
 * (formerly known as the TypeRel builder).
 * This includes direction and cardinality, and the type of nodes itself. These fields are the only ones that can be changed
 * (node type data cannot be changed except through the use of an administration module).
 * This interface is therefor not a real mmbase 'object' in itself - it exists of two objects joined together.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface RelationTypeInterface extends NodeTypeInterface {
	/** 
	 * Directionality constant : uni-directional
     */
    public final static int UNIDIRECTIONAL = 1;

    /** 
	 * Directionality constant : bi-directional
     */
    public final static int BIDIRECTIONAL = 2;

	/**
	 * gets the role of the source to the destination
	 * @return the role
	 */
	public String getForwardRole();

	/**
	 * gets the role of the destination to the source
	 * @return the role
	 */
	public String getReciprocalRole();

 	/**
     * Retrieves the directionality for this type (the default assigned to a new relation).
     * @return one of the directionality constants
     */
    public int getDirectionality();

    /**
     * Retrieves the type of node that can act as the source of a relation of this type.
     * @return the source type
     */
    public NodeTypeInterface getSourceType();

	/**
     * Retrieves the type of node that can act as the destination of a relation of this type.
     * @return the destination type
     */
    public NodeTypeInterface getDestinationType();
}
