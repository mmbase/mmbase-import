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


	/**
     * Search nodes in a cloud accoridng to a specified filter.
     * @param nodes The numbers of the nodes to start the search with. These have to be a member of the first node type
     *      listed in the nodetypes parameter. The syntax is a comma-seperated lists of node ids.
     *      Example : '112' or '1,2,14'
     * @param nodetypes The nodetype chain. The syntax is a comma-seperated lists of node type names.
     *      The search is formed by following the relations between successive nodetypes in the list. It is possible to explicitly supply
     *      a relation type by placing the name of the type between two nodetypes to search.
     *      Example: 'company,people' or 'typedef,authrel,people'.
     * @param fields The fieldnames to return (comma seperated). This can include the name of the nodetype in case of fieldnames that are used by more than one type (i.e number).
     *      Fieldnames are accessible in the nodes returned in the same format (i.e. with typeindication) as they are specified in this parameter.
     *      Examples: 'people.lastname', 'typedef.number,authrel.creat,people.number'
     * @param where The contraint. this is in essence a SQL where clause, using the type names from the typenodes as tablenames.
     *      Examples: "people.email IS NOT NULL", "(authrel.creat=1) and (people.lastname='admin')"
     * @param order the fieldnames on which you want to sort. Identical in syntax to the fields parameter.
     * @param direction A list of values containing, for each field in the order parameter, a value inidcating whether the sort is
     *      ascending (<code>UP</code>) or descending (<code>DOWN</code>). If less values are syupplied then there are fields in order,
     *      The first value in the list is used for teh remainig fields. Default value is <code>'UP'</code>.
     *      Examples: 'UP,DOWN,DOWN'
     * @param distinct <code>True> indicates the records returned need to be distinct. <code>False</code> indicates double values can be returned.
     * @return a <code>List</code> of found nodes
     */
    public List search(String nodes, String nodeTypes, String fields, String where, String sorted, String direction, boolean distinct);
}
