/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Enumeration;

/**
 * A relation within the cloud.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface RelationInterface extends NodeInterface {

	/** 
	 * Retrieves the source of the relation
	 * @return the source node
	 */
	public NodeInterface getSource();

	/**
	 * Retrieves the destination of the relation
	 * @return the destination node
	 */
	public NodeInterface getDestination();

	/** 
	 * set the source of the relation
	 * @param the source node
	 */
	public void setSource(NodeInterface node);

	/**
	 * set the destination of the relation
	 * @param the destination node
	 */
	public void setDestination(NodeInterface node);

 	/**
     * Retrieves the relation type used
     * @return the relation type
     */
    public RelationTypeInterface getRelationType();
}
