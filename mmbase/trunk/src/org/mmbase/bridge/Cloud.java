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
	 * Retrieve the node from the cloud
	 * @param nodenumber the number of the node as a string
	 * @return the requested node
	 */
	public Node getNode(String nodenumber);
	
	/**
	 * Retrieves the node with given aliasname
	 * @param aliasname the aliasname of the node
	 * @return the requested node
	 */
	public Node getNodeByAlias(String aliasname);

 	/**
     * Retrieves all node managers (aka builders) available in this cloud
     * @return an <code>Iterator</code> containing all node managers
     */
    public NodeManagerList getNodeManagers();

	/**
     * Retrieves a node manager (aka builder)
     * @param nodeManagerName name of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     */
    public NodeManager getNodeManager(String nodeManagerName);

 	/**
     * Retrieves a RelationManager
     * @param sourceManagerName name of the NodeManager of the source node
     * @param destinationManagerName name of the NodeManager of the destination node
     * @param roleName name of the role
     * @return the requested RelationManager
     */
    public RelationManager getRelationManager(String sourceManagerName, String destinationManagerName, String roleName);

 	/**
     * Retrieves all RelationManagers
     * @return a list of RelationManagers
     */
    public RelationManagerList getRelationManagers();
	
	/**
    * Retrieves the context for this cloud
    * @return the cloud's context
    */
    public CloudContext getCloudContext();

    /**
     * Creates a transaction on this cloud with a generic ID.
     * @return a <code>Transaction</code> on this cloud
     */
    public Transaction createTransaction();

    /**
     * Creates a named transaction on this cloud.
     * @param name an unique name to use for the transaction
     * @return a <code>Transaction</code> on this cloud
     */
    public Transaction createTransaction(String name);

    /**
     * Creates a transaction on this cloud.
     * @param name the unique name of the transaction to open
     * @return the identified <code>Transaction</code>
     */
    public Transaction openTransaction(String name);
   	
  	/**
     * Logs on a user.
     * This results in an environment (a cloud) in which the user is registered.
	 * @param authenticatorName name of the authentication method to sue
	 * @param parameters parameters for the authentication
	 * @return <code>true</code> if succesful (should throw exception?)
     */
    public boolean logon(String authenticatorName, Object[] parameters);
  	
  	/**
     * Logs off a user.
     * Resets the user's context to 'anonymous'
     */
    public void logoff();

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
	
	/**
     * Search nodes in a cloud accoridng to a specified filter.
     * @param nodes The numbers of the nodes to start the search with. These have to be a member of the first NodeManager
     *      listed in the nodeManagers parameter. The syntax is a comma-seperated lists of node ids.
     *      Example : '112' or '1,2,14'
     * @param nodeManagers The NodeManager chain. The syntax is a comma-seperated lists of NodeManager names.
     *      The search is formed by following the relations between successive NodeManagers in the list. It is possible to explicitly supply
     *      a RelationManager by placing the name of the manager between two NodeManagers to search.
     *      Example: 'company,people' or 'typedef,authrel,people'.
     * @param fields The fieldnames to return (comma seperated). This can include the name of the NodeManager in case of fieldnames that are used by
     *      more than one manager (i.e number).
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with manager indication) as they are specified in this parameter.
     *      Examples: 'people.lastname', 'typedef.number,authrel.creat,people.number'
     * @param where The contraint. this is in essence a SQL where clause, using the NodeManager names from the nodes as tablenames.
     *      Examples: "people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param order the fieldnames on which you want to sort. Identical in syntax to the fields parameter.
     * @param direction A list of values containing, for each field in the order parameter, a value indicating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      The first value in the list is used for teh remainig fields. Default value is <code>'UP'</code>.
     *      Examples: 'UP,DOWN,DOWN'
     * @param distinct <code>True> indicates the records returned need to be distinct. <code>False</code> indicates double values can be returned.
     * @return a <code>List</code> of found (virtual) nodes
     */
    public NodeList getList(String nodes, String nodeManagers, String fields, String where, String sorted, String direction, boolean distinct);
}
