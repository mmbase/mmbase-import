/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Iterator;
import org.mmbase.module.core.NodeInterface; 

/**
 * A Cloud is a collection of Nodes (and relations that are also nodes).
 * A Cloud is tied to one or more CLoudContexts (which reside on various VMs).
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface CloudInterface {

	/**
	 * Retrieve the node from the cloud
	 * @param nodenumber the number of the node
	 * @return the requested node
	 */
	public NodeInterface getNode(int nodenumber);

	/**
	 * Retrieves the node with given aliasname
	 * @param aliasname the aliasname of the node
	 * @return the requested node
	 */
	public NodeInterface getNode(String aliasname);

 	/**
     * Retrieves all node types (aka builders) available in this cloud
     * @return an <code>Iterator</code> containing all node types
     */
    public Iterator getNodeTypes();

	/**
     * Retrieves a node type (aka builder)
     * @param nodeTypeName name of the node type to retrieve
     * @return the requested node type
     */
    public NodeTypeInterface getNodeType(String nodeTypeName);

 	/**
     * Retrieves a node type (aka builder)
     * @param nodeTypeId Number of the node type to retrieve
     * @return the requested node type
     */
    public NodeTypeInterface getNodeType(int nodeTypeId);

	/**
     * Creates a node of a specific type
     * @param nodeTypeName name of the node type defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public NodeInterface createNode(String nodeTypeName);

	/**
     * Creates a node of a specific type
     * @param nodeTypeId number of the node type defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public NodeInterface createNode(int nodeTypeId);
 
	/**
	 * Creates a node of a specific type
     * @param nodeType the node type defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public NodeInterface createNode(NodeTypeInterface nodeType);

	/**
     * Retrieves the context for this cloud
     * @return the cloud's context
     */
    public CloudContextInterface getCloudContext();

  	/**
     * Logs the current user on under a given logon name.
     * @param account the useraccount to log on to
     * @param password the user's password
     * @return <code>true</code> if logon was succesful, <code>false</code> otherwise.
     * public boolean logOn(String account,String password);
     */

    /**
     * Logs the current user off.
     * public void logOff();
     */

  	/**
     * Retrieves the cloud's name (this is an unique identifier).
     * @return the cloud's name
     */
    public String getName();

    /**
     * Retrieves the description of the cloud
	 * @return return a description of the cloud
     */
    public String getDescription();
}
