/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Enumeration;

/**
 *
 * @author Rob Vermeulen
 */
public interface RelationInterface extends NodeInterface {

	/** 
	 * get the source of the relation
	 * @return the source Node
	 */
	public NodeInterface getSource();

	/**
	 * get the destination of the relation
	 * @return the destination Node
	 */
	public NodeInterface getDestination();

	/** 
	 * set the source of the relation
	 * @param the source Node
	 */
	public void setSource(NodeInterface node);

	/**
	 * set the destination of the relation
	 * @param the destination Node
	 */
	public void setDestination(NodeInterface node);
}
