/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Enumeration;
import org.mmbase.module.core.NodeInterface; 

/**
 *
 * @author Rob Vermeulen
 */
public interface CloudInterface {

	/**
	 * give the Node 
	 * @param the number of the Node
	 * @return Node
	 */
	public NodeInterface getNode(int nodenumber);

	/**
	 * give the Node with given aliasname
	 * @param the aliasname of the Node
	 * @return Node with given aliasname
	 */
	public NodeInterface getNode(String aliasname);

	/**
	 * gets all Builders
	 * @return a enumeration of all Builders
	 */
	public Enumeration getBuilders();
	
	/**
	 * gets a builder
	 * @return the builder with given buildername 
	 */
	public BuilderInterface getBuilder(String buildername);

	/**
	 * creates a node of a specific type
	 * @param name of the nodetype
	 * @return the builder with given buildername 
	 */
	public NodeInterface createNode(String buildername);
 }
