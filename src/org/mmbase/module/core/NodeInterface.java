/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.core;
import java.util.Iterator;

/**
 * Describes an object in the cloud.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface NodeInterface {

  	/**
     * Retrieves the cloud where this node is part of.
     */
    public CloudInterface getCloud();

	/**
     * Retrieves the type of this node
     */
    public NodeTypeInterface getNodeType();
	
	/**
     * Retrieves the node ID
     */
    public int getNodeID();
	
	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setValue(String attribute, String value); 

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public Object getValue(String fieldName);

	/**
	 * Commit the node to the database
	 */
	public void commit();

	/**
	 * Removes the Node
	 */
	public void remove(); 

	/**
	 * Converts the node to a string
	 */
	 public String toString();

	/**
	 * Removes all relations of the node
	 */
	public void removeRelations();

	/**
 	 * Removes all relations of certain type of this node
	 * @param type of relation
	 */
	public void removeRelations(String type);

	/**
	 * Retrieve all relations of this node
	 * @return all relations of Node
	 */
	public Iterator getRelations();

	/**
	 *gets all relations of a certain type
	 * @param type of relation
	 * @return all relations of the Node of a certain type
	 */
	public Iterator getRelations(String type);
	
	/**
	 * Count the relations attached to the Node
	 * @return number of relations
	 */
	public int countRelations();

	/**
	 * Count the relations of a specific type attached to the Node 
	 * @return number of relations of a specific type
	 */
	public Integer countRelations(String type);

	/**
	 * Retrieve all related Nodes
	 * @return all related Nodes
	 */
	public Iterator getRelatedNodes();

	/**
	 * Retrieve all related nodes of a certain type
	 * @return all related nodes of a certain type
	 */
	public Iterator getRelatedNodes(String type);

	/**
     * Retrieves the aliases of this node
     * @return an Iterator with the alias anmes
     */
    public Iterator getAliases();

	/**
     * Add an alias for this node
     * @param aliasName the name of the alias (need to be unique)
     */
    public void addAlias(String aliasName);

    /**
     * Remove an alias of this node
     * @param aliasName the name of the alias
     */
    public void removeAlias(String aliasName);
}
