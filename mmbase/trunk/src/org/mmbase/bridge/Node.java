/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.module.core.*;
import java.util.List;

/**
 * Describes an object in the cloud.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public interface Node {

  	/**
     * Retrieves the cloud where this node is part of.
     */
    public Cloud getCloud();

	/**
     * Retrieves the NodeManager of this node
     */
    public NodeManager getNodeManager();
	
	/**
     * Retrieves the node ID
     */
    public int getNodeID();
	
	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setValue(String attribute, Object value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setIntValue(String attribute, int value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setFloatValue(String attribute, float value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setDoubleValue(String attribute, double value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setByteValue(String attribute, byte[] value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setLongValue(String attribute, long value); 

	/** 
	 * Set the value of certain attribute
	 * @param attribute name of field
	 * @param value of attribute
	 */
	public void setStringValue(String attribute, String value); 

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public Object getValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public int getIntValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public float getFloatValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public long getLongValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public double getDoubleValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public byte[] getByteValue(String fieldName);

	/** 
	 * Retrieves the value of certain attribute
	 * @param name of attribute you want 
	 * @return value of attribute
	 */
	public String getStringValue(String fieldName);

	/**
	* Commit the node to the database.
	* Makes this node and/or the changes made to this node visible to the cloud.
    * If this method is called for the first time on this node it will make
    * this node visible to the cloud, otherwise the modifications made to
    * this node using the set methods will be made visible to the cloud.
    * This action fails if the current node is not in edit mode.
    * If the node is in a transaction, nothing happens - actual committing occurs through the transaction.
	*/
	public void commit();

	/**
	 * Cancel changes to a node
	 * This fails if the current node is not in edit mode.
	 * If the node is in a transaction, nothing happens - actual committing occurs through the transaction.
	 */
	public void cancel();
	
	/**
	 * Removes the Node
	 */
	public void remove(); 

	/**
	 * Removes the Node. Also removes relations if any exist.
	 */
	public void removeAll();
	
	/**
	 * Converts the node to a string
	 */
	 public String toString();

	/**
	 * Removes all relations of the node
	 */
	public void removeAllRelations();

	/**
 	 * Removes all relations of certain type of this node
	 * @param type of relation
	 */
	public void removeRelations(String type);

	/**
	 * Retrieve all relations of this node
	 * @return a <code>List</code> of all relations of Node
	 */
	public List getAllRelations();

	/**
	 * Gets all relations of a certain type
	 * @param type of relation
	 * @return a <code>List</code> of all relations of the Node of a certain type
	 */
	public List getRelations(String type);
	
	/**
	 * Count the relations attached to the Node
	 * @return number of relations
	 */
	public int countAllRelations();

	/**
	 * Count the relations of a specific type attached to the Node 
	 * @return number of relations of a specific type
	 */
	public int countRelations(String type);

	/**
	 * Retrieve all related Nodes
	 * @return a <code>List</code> of all related Nodes
	 */
	public List getAllRelatedNodes();

	/**
	 * Retrieve all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return a <code>List</code> of all related nodes of the given manager
	 */
	public List getRelatedNodes(String type);

	/**
	 * Count all related nodes maintained by a given NodeManager
	 * @param type name of the NodeManager of the related nodes
	 * @return number of related nodes of a specific type
	 */
	public int countRelatedNodes(String type);
	
	/**
     * Retrieves the aliases of this node
     * @return a <code>List</code> with the alias anmes
     */
    public List getAliases();

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

    /**
     * Adds a relation to this node
     * @param destinationNode the node to which you want to relate this node
	 * @param relationManager The relation manager you want to use
	 * @return the added relation
     */
    public Relation addRelation(Node destinationNode, RelationManager relationManager);
}
