/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.List;

/**
 * A Cloud is a collection of Nodes (and relations that are also nodes).
 * A Cloud is tied to one or more CLoudContexts (which reside on various VMs).
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface Cloud {

	/**
	 * Retrieve the node from the cloud
	 * @param nodenumber the number of the node
	 * @return the requested node
	 */
	public Node getNode(int nodenumber);

	/**
	 * Retrieves the node with given aliasname
	 * @param aliasname the aliasname of the node
	 * @return the requested node
	 */
	public Node getNode(String aliasname);

 	/**
     * Retrieves all node types (aka builders) available in this cloud
     * @return an <code>Iterator</code> containing all node types
     */
    public List getNodeTypes();

	/**
     * Retrieves a node type (aka builder)
     * @param nodeTypeName name of the node type to retrieve
     * @return the requested node type
     */
    public NodeType getNodeType(String nodeTypeName);

	/**
     * Retrieves a node type (aka builder)
     * @param nodeTypeID number of the node type to retrieve
     * @return the requested node type
     */
    public NodeType getNodeType(int nodeTypeID);
 	
 	/**
     * Retrieves a relation type
     * @param sourceTypeName name of the type of the source node
     * @param destinationTypeName name of the type of the destination node
     * @param roleName name of the role
     * @return the requested node type
     */
    public RelationType getRelationType(String sourceTypeName, String destinationTypeName, String roleName);

	/**
     * Creates a node of a specific type
     * @param nodeTypeName name of the node type defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public Node createNode(String nodeTypeName);

	/**
     * Creates a node of a specific type
     * @param nodeTypeID number of the node type defining the node structure
     * @return the newly created (but not yet committed) node
     */
    public Node createNode(int nodeTypeID);
	
	/**
     * Retrieves the context for this cloud
     * @return the cloud's context
     */
    public CloudContext getCloudContext();

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
